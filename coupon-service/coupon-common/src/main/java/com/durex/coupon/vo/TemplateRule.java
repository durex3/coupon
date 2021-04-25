package com.durex.coupon.vo;

import com.durex.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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
    private Expiration expiration;

    /**
     * 优惠券的折扣规则
     */
    private Discount discount;

    /**
     * 每个人最多领几张
     */
    private Integer limitation;

    /**
     * 优惠券的使用范围
     */
    private Usage usage;

    /**
     * 权重，可以和哪些优惠券进行组合使用，同一类优惠券不能叠加
     * List[]，优惠券唯一编码
     */
    private String weight;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Expiration {

        /**
         * 有效期规则code -> PeriodType
         */
        private Integer period;

        /**
         * 有效间隔，只对变动型有效
         */
        private Integer gap;

        /**
         * 优惠券模版失效日期
         */
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
        private Integer quota;

        /**
         * 基准，需要满多少才可以用
         */
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
        private String province;

        /**
         * 城市
         */
        private String city;

        /**
         * 商品类型[文娱, 生鲜, 家具, 全部]
         */
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotBlank(province) &&
                    StringUtils.isNotBlank(city) &&
                    StringUtils.isNotBlank(goodsType);
        }
    }
}
