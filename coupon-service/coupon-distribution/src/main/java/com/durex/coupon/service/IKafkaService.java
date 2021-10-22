package com.durex.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka 相关的服务接口定义
 *
 * @author liugelong
 * @date 2021/10/21 10:32 上午
 */
public interface IKafkaService {

    /**
     * <h2>消费优惠券 Kafka 消息</h2>
     *
     * @param consumerRecord {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> consumerRecord);
}
