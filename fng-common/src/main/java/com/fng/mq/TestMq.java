package com.fng.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @Description
 * @Author wuou
 * @Date 2021/11/24 上午9:59
 * @Version 1.0.0
 */
public class TestMq {
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("helloGp");
        producer.setNamesrvAddr("59.36.75.54:3389");
        producer.setVipChannelEnabled(false);
        producer.start();
        Message msg = new Message("myTopic001","TagB","b-就这就这?????".getBytes());
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("Ok");
                System.out.println(sendResult);
                producer.shutdown();
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println(throwable.getMessage());
                producer.shutdown();
            }
        });

    }
}
