package com.durex.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的token
 *
 * @author liugelong
 * @date 2021/4/21 9:21 下午
 */
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        String token = request.getParameter("token");
        if (StringUtils.isEmpty(token)) {
            log.error("error: token is empty");
            return fail(401, "token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
