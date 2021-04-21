package com.durex.coupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 限量过滤器
 *
 * @author liugelong
 * @date 2021/4/21 9:37 下午
 */
@Slf4j
@Component
public class RateLimiterFilter extends AbstractPreZuulFilter {

    private final RateLimiter rateLimiter = RateLimiter.create(2.0);

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        if (rateLimiter.tryAcquire()) {
            log.info("get rate token success");
            return success();
        } else {
            log.error("rate limit: {}", request.getRequestURI());
            return fail(401, "rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
