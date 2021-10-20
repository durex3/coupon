package com.durex.coupon.schedule;

import com.durex.coupon.constant.Constant;
import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.repository.CouponTemplateRepository;
import com.durex.coupon.vo.TemplateRule;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author liugelong
 * @date 2021/4/28 9:24 下午
 */
@Slf4j
@Component
public class ScheduleTask {

    @Resource
    private CouponTemplateRepository templateRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {
        log.info("start to expire coupon template");
        // 获取所有未过期的优惠券模版
        List<CouponTemplate> templateList = templateRepository.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templateList)) {
            log.info("done to expire coupon template");
            return;
        }
        Date cur = new Date();
        List<CouponTemplate> expiredTemplateList = Lists.newArrayListWithExpectedSize(
                templateList.size()
        );
        List<String> expiredKeyList = Lists.newArrayList();
        templateList.forEach((CouponTemplate template) -> {
            TemplateRule rule = template.getRule();
            // 已经过期
            if (rule.getExpiration().getDeadline() < cur.getTime()) {
                template.setExpired(true);
                expiredTemplateList.add(template);
                String redisKey = String.format(
                        "%s%s",
                        Constant.RedisPrefix.COUPON_TEMPLATE,
                        template.getId().toString()
                );
                expiredKeyList.add(redisKey);
            }
        });
        if (CollectionUtils.isNotEmpty(expiredTemplateList)) {
            log.info("expire coupon template num: {}", templateRepository.saveAll(expiredTemplateList));
            stringRedisTemplate.delete(expiredKeyList);
        }
    }
}
