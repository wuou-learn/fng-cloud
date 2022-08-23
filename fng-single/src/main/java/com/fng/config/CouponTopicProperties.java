package com.fng.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wuou
 */
@Component
@ConfigurationProperties(prefix = "fng.coupon.mq.topic")
public interface CouponTopicProperties {

    String TOPIC = "${fng.mq.topic.domainTopicConfig.coupon:topic_coupon}";

}
