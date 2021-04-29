package com.durex.coupon.request;

import com.durex.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 优惠券模版创建请求对象
 *
 * @author liugelong
 * @date 2021/4/26 9:08 下午
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TemplateRequest {

    /**
     * 优惠券名称
     */
    @NotBlank(message = "优惠券模板名称不能为空")
    private String name;

    /**
     * 优惠券 logo
     */
    @NotBlank(message = "优惠券模板logo不能为空")
    private String logo;

    /**
     * 优惠券描述
     */
    @NotBlank(message = "优惠券模板描述不能为空")
    private String desc;

    /**
     * 优惠券分类
     */
    @NotBlank(message = "优惠券模板分类不能为空")
    private String category;

    /**
     * 产品线
     */
    @NotBlank(message = "产品线不能为空")
    private Integer productLine;

    /**
     * 总数
     */
    @Size(min = 1, message = "优惠券总数必须大于1")
    @NotNull(message = "优惠券总数不能为空")
    private Integer count;

    /**
     * 创建用户
     */
    @NotNull(message = "创建用户不能为空")
    private Long userId;

    /**
     * 目标用户
     */
    @NotNull(message = "目标用户不能为空")
    private Integer target;

    /**
     * 优惠券规则
     */
    @Valid
    @NotNull(message = "优惠券模板规则不能为空")
    private TemplateRule rule;
}
