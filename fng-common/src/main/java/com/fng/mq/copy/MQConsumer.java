package com.fng.mq.copy;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQConsumer {

    //保存标识的集合
    static private Map<String, String> logMap = new HashMap<>();

    public static void main(String[] args) throws MQClientException {
        //创建消费者
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer("rmq-group");
        //设置NameServer地址
        consumer.setNamesrvAddr("59.36.75.54:3389");
        //设置消费者实例名称
        consumer.setInstanceName("consumer");
        //订阅topic
        consumer.subscribe("wn04","TagA");
        //监听消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                String key = null;
                String msgId = null;
                try {
                    for (MessageExt msg : list) {
                        key = msg.getKeys();
                        //判断集合当中有没有存在key,存在就不需要重试,不存在先存key再回来重试后消费消息
                        if (logMap.containsKey(key)) {
                            // 无需继续重试。
                            System.out.println("key:"+key+",无需重试...");
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        msgId = msg.getMsgId();
                        System.out.println("key:" + key + ",msgid:" + msgId + "---" + new String(msg.getBody()));
                        //模拟异常
                        int i = 1 / 0;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    //重试
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                } finally {
                    //保存key
                    logMap.put(key, msgId);
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("Consumer Started...");

    }
}
