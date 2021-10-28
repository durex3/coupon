package com.durex.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.constant.Constant;
import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.dao.CouponDao;
import com.durex.coupon.entity.Coupon;
import com.durex.coupon.feign.SettlementClient;
import com.durex.coupon.feign.TemplateClient;
import com.durex.coupon.service.IRedisService;
import com.durex.coupon.service.IUserService;
import com.durex.coupon.vo.AcquireTemplateRequest;
import com.durex.coupon.vo.CouponClassify;
import com.durex.coupon.vo.CouponKafkaMessage;
import com.durex.coupon.vo.CouponTemplateSDK;
import com.durex.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h1>用户微服务相关的接口实现</h1>
 * 所有的操作过程, 状态都保存在 Redis 中, 并通过 Kafka 把消息传递到 MySQL 中
 * 为什么使用 Kafka, 而不是直接使用 SpringBoot 中的异步处理 ?
 *
 * @author liugelong
 * @date 2021/10/28 9:56 下午
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    /**
     * Coupon Dao
     */
    private final CouponDao couponDao;

    /**
     * Redis 服务
     */
    private final IRedisService redisService;

    /**
     * 模板微服务客户端
     */
    private final TemplateClient templateClient;

    /**
     * 结算微服务客户端
     */
    private final SettlementClient settlementClient;

    /**
     * Kafka 客户端
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserServiceImpl(CouponDao couponDao, IRedisService redisService,
                           TemplateClient templateClient,
                           SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Coupon> getCouponListByStatus(Long userId, Integer status) {

        List<Coupon> curCached = redisService.getCachedCouponList(userId, status);
        List<Coupon> preTarget;

        if (CollectionUtils.isNotEmpty(curCached)) {
            log.info("coupon cache is not empty: {}, {}", userId, status);
            preTarget = curCached;
        } else {
            log.info("coupon cache is empty, get coupon from db: {}, {}",
                    userId, status);
            List<Coupon> dbCouponList = couponDao.findAllByUserIdAndStatus(
                    userId, CouponStatus.of(status)
            );

            // 如果数据库中没有记录, 直接返回就可以, Cache 中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCouponList)) {
                log.info("current user do not have coupon: {}, {}", userId, status);
                return dbCouponList;
            }

            // 填充 dbCoupons的 templateSDK 字段
            Map<Integer, CouponTemplateSDK> id2TemplateSDK =
                    templateClient.findIds2TemplateSDK(
                            dbCouponList.stream()
                                    .map(Coupon::getTemplateId)
                                    .collect(Collectors.toList())
                    ).getData();

            dbCouponList.forEach(
                    dc -> dc.setTemplateSDK(
                            id2TemplateSDK.get(dc.getTemplateId())
                    )
            );
            // 数据库中存在记录
            preTarget = dbCouponList;
            // 将记录写入 Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }

        // 将无效优惠券剔除
        preTarget = preTarget.stream().filter(coupon -> coupon.getId() != -1)
                .collect(Collectors.toList());

        // 如果当前获取的是可用优惠券, 还需要做对已过期优惠券的延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            // 如果已过期状态不为空, 需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: " +
                        "{}, {}", userId, status);
                redisService.addCouponToCache(
                        userId,
                        classify.getExpired(),
                        CouponStatus.EXPIRED.getCode()
                );

                List<Integer> expiredIdList = classify.getExpired().stream()
                        .map(Coupon::getId)
                        .collect(Collectors.toList());

                // 发送到 kafka 中做异步处理
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(
                                new CouponKafkaMessage(
                                        CouponStatus.EXPIRED.getCode(),
                                        expiredIdList
                                )
                        )
                );
            }
            preTarget = classify.getUsable();
        }

        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> getAvailableTemplate(Long userId) {

        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKList = templateClient.getAllUsableTemplate().getData();

        log.info("Find All Template(From TemplateClient) Count: {}", templateSDKList.size());

        // 过滤过期的优惠券模板
        templateSDKList = templateSDKList.stream().filter(
                t -> t.getRule().getExpiration().getDeadline() > curTime
        ).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKList.size());

        // key 是 TemplateId
        // value 中的 left 是 Template limitation, right 是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKList.size());
        templateSDKList.forEach(
                t -> limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(), t)
                )
        );

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        // 获取用户可用的优惠券
        List<Coupon> userUsableCouponList = getCouponListByStatus(
                userId, CouponStatus.USABLE.getCode()
        );

        log.info("Current User Has Usable Coupons: {}, {}", userId, userUsableCouponList.size());

        Map<Integer, List<Coupon>> templateId2CouponList = userUsableCouponList
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 根据 Template 的 Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            // 领取的优惠券超过了领取限制
            if (templateId2CouponList.containsKey(k) &&
                    templateId2CouponList.get(k).size() >= limitation) {
                return;
            }
            result.add(templateSDK);
        });

        return result;
    }

    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) {
        return null;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) {
        return null;
    }
}
