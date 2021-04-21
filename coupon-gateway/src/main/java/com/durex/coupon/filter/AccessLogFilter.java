package com.durex.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 记录请求时间的过滤器
 *
 * @author liugelong
 * @date 2021/4/21 9:59 下午
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        // PreRequestFilter中获取请求时间戳
        long startTime = (long) context.get("startTime");
        long duration = System.currentTimeMillis() - startTime;
        log.info("uri: {}, duration: {}", uri, duration);
        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
