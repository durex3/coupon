package com.durex.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.constant.Constant;
import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.dao.CouponDao;
import com.durex.coupon.entity.Coupon;
import com.durex.coupon.service.IKafkaService;
import com.durex.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h1>Kafka 相关的服务接口实现</h1>
 * 核心思想: 是将 Cache 中的 Coupon 的状态变化同步到 DB 中
 *
 * @author liugelong
 * @date 2021/10/28 9:39 下午
 */
@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    /**
     * Coupon Dao
     */
    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    @KafkaListener(topics = {Constant.TOPIC}, groupId = "coupon-1")
    @Override
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> consumerRecord) {
        if (consumerRecord.value() != null) {
            Object message = consumerRecord.value();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );

            log.info("Receive CouponKafkaMessage: {}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCouponList(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCouponList(couponInfo, status);
                    break;
            }
        }
    }

    private void processExpiredCouponList(CouponKafkaMessage couponInfo, CouponStatus status) {
        // TODO 给用户发送推送
        processCouponsByStatus(couponInfo, status);
    }

    private void processUsedCouponList(CouponKafkaMessage couponInfo, CouponStatus status) {
        // TODO 给用户发送短信
        processCouponsByStatus(couponInfo, status);
    }

    /**
     * <h2>根据状态处理优惠券信息</h2>
     *
     * @param couponInfo 优惠券消息
     * @param status     状态
     */
    private void processCouponsByStatus(CouponKafkaMessage couponInfo, CouponStatus status) {
        List<Coupon> couponList = couponDao.findAllById(couponInfo.getIdList());
        if (CollectionUtils.isEmpty(couponList)
                || couponList.size() != couponInfo.getIdList().size()) {
            log.error("Can Not Find Right Coupon Info: {}",
                    JSON.toJSONString(couponInfo));
            // TODO 发送邮件
            return;
        }

        couponList.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}", couponDao.saveAll(couponList).size());
    }
}
