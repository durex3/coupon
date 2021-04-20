package com.durex.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 自定义网关过滤器抽象类
 *
 * @author liugelong
 * @date 2021/4/20 11:36 下午
 */
public abstract class AbstractZuulFilter extends ZuulFilter {

    /**
     * 用于在过滤器之间传递消息，保持在ThreadLocal
     */
    RequestContext context;
    private static final String NEXT = "next";

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    @Override
    public Object run() throws ZuulException {
        context = RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object success() {
        context.set(NEXT, true);
        return null;
    }

    Object fail(int code, String message) {
        context.set(NEXT, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(
                String.format(
                        "{\"result\": \"%s!\"}",
                        message
                )
        );
        return null;
    }
}
