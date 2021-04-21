package com.durex.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * post抽象过滤器
 *
 * @author liugelong
 * @date 2021/4/21 12:04 上午
 */
public abstract class AbstractPostZuulFilter extends AbstractZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}