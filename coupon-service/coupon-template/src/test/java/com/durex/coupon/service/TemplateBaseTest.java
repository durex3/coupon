package com.durex.coupon.service;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.vo.CouponTemplateSDK;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author liugelong
 * @date 2021/4/29 11:23 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateBaseTest {

    @Resource
    private ITemplateBaseService templateBaseService;

    @Test
    public void testGetById() {
        CouponTemplate couponTemplate = templateBaseService.getById(13);
        System.out.println(JSON.toJSONString(couponTemplate));
    }


    @Test
    public void testGetAllUsableTemplate() {
        List<CouponTemplateSDK> allUsableTemplate = templateBaseService.getAllUsableTemplate();
        System.out.println(JSON.toJSONString(allUsableTemplate));
    }


    @Test
    public void testGetIds2TemplateSDK() {
        Map<Integer, CouponTemplateSDK> map = templateBaseService.getIds2TemplateSDK(Lists.newArrayList(13));
        System.out.println(JSON.toJSONString(map));
    }

}
