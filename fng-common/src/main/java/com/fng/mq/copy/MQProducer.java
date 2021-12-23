package com.fng.mq.copy;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class MQProducer {
    public static void main(String[] args) throws MQClientException {
        //创建生产者
        DefaultMQProducer producer=new DefaultMQProducer("rmq-group");
        //设置NameServer地址
        producer.setNamesrvAddr("59.36.75.54:3389");
        //设置生产者实例名称
        producer.setInstanceName("producer");
        //启动生产者
        producer.start();
        try {
            //发送消息
            for (int i=0;i<1;i++){
                Thread.sleep(1000); //每秒发送一次
                //创建消息
                Message msg = new Message("wn04", // topic 主题名称
                        "TagA", // tag 临时值
                        ("w-"+i).getBytes()// body 内容
                );
                //消息的唯一標識
                msg.setKeys(System.currentTimeMillis() + "");
                //发送消息
                SendResult sendResult=producer.send(msg);
                System.out.println(sendResult.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

}
