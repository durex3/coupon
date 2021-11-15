package com.durex.coupon.service;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.entity.Coupon;
import com.durex.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author liugelong
 * @date 2021/11/15 9:59 下午
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    private Long fakeUserId = 1L;
    @Autowired
    public IUserService userService;

    @Test
    public void testGetCouponsByStatus() {
        List<Coupon> couponList = userService.getCouponListByStatus(fakeUserId, CouponStatus.USABLE.getCode());
        log.info(JSON.toJSONString(couponList));
    }

    @Test
    public void testGetAvailableTemplate() {
        List<CouponTemplateSDK> templateSDKList = userService.getAvailableTemplate(fakeUserId);
        log.info(JSON.toJSONString(templateSDKList));
    }
}
