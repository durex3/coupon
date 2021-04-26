package com.durex.coupon.vo;

import com.durex.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 优惠券规则定义
 *
 * @author liugelong
 * @date 2021/4/25 9:13 下午
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TemplateRule {

    /**
     * 优惠券的过期规则
     */
    @Valid
    @NotNull(message = "优惠券的过期规则不能为空")
    private Expiration expiration;

    /**
     * 优惠券的折扣规则
     */
    @Valid
    @NotNull(message = "优惠券的折扣规则不能为空")
    private Discount discount;

    /**
     * 每个人最多领几张
     */
    @NotNull(message = "优惠券每人最多领券的数量不能为空")
    private Integer limitation;

    /**
     * 优惠券的使用范围
     */
    @Valid
    @NotNull(message = "优惠券的使用范围不能为空")
    private Usage usage;

    /**
     * 权重，可以和哪些优惠券进行组合使用，同一类优惠券不能叠加
     * List[]，优惠券唯一编码
     */
    @NotBlank(message = "优惠券权重不能为空")
    private String weight;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Expiration {

        /**
         * 有效期规则code -> PeriodType
         */
        @NotNull(message = "优惠券的有效期规则不能为空")
        private Integer period;

        /**
         * 有效间隔，只对变动型有效
         */
        @NotNull(message = "优惠券的有效间隔不能为空")
        private Integer gap;

        /**
         * 优惠券模版失效日期
         */
        @NotNull(message = "优惠券模版失效日期不能为空")
        private Long deadline;

        public boolean validate() {
            long currentTimeMillis = System.currentTimeMillis();
            return PeriodType.of(period) != null && gap > 0 && currentTimeMillis < deadline;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Discount {

        /**
         * 额度：满减（20），折扣（85），立减（10）
         */
        @NotNull(message = "优惠券模版的额度不能为空")
        private Integer quota;

        /**
         * 基准，需要满多少才可以用
         */
        @NotNull(message = "优惠券模版的基准不能为空")
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Usage {

        /**
         * 省份
         */
        @NotNull(message = "优惠券模版使用省份不能为空")
        private String province;

        /**
         * 城市
         */
        @NotNull(message = "优惠券模版使用城市不能为空")
        private String city;

        /**
         * 商品类型[文娱, 生鲜, 家具, 全部]
         */
        @NotNull(message = "优惠券模版使用商品类型不能为空")
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotBlank(province) &&
                    StringUtils.isNotBlank(city) &&
                    StringUtils.isNotBlank(goodsType);
        }
    }
}
