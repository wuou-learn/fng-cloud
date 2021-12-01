package com.fng.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @Description
 * @Author wuou
 * @Date 2021/11/24 上午9:59
 * @Version 1.0.0
 */
public class TestMq {
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        TransactionMQProducer producer = new TransactionMQProducer("helloGp");
        producer.setNamesrvAddr("59.36.75.54:3389");
        producer.setVipChannelEnabled(false);
        producer.start();
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                // 事务执行
                System.out.println(new String(message.getBody()));
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // 事务检查 是否成功
                System.out.println(new String(messageExt.getBody()));

                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });


        Message msg = new Message("myTopic001",null,"TagA","事务消息!!!!--A2".getBytes());

        producer.sendMessageInTransaction(msg,null);
    }


    public void sendMesg() throws MQClientException, RemotingException, InterruptedException {
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
