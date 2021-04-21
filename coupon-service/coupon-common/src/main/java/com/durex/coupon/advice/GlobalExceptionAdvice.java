package com.durex.coupon.advice;

import com.durex.coupon.exception.CouponException;
import com.durex.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author liugelong
 * @date 2021/4/21 11:30 下午
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(CouponException e) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(e.getMessage());
        return response;
    }
}
