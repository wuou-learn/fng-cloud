package com.fng.mq.Order;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import java.util.List;
public class Producer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("quickstart_producer");
        producer.setNamesrvAddr("59.36.75.54:3389");
        producer.start();
        for(int i = 0;i < 6 ;i++){
            int orderId=(int)((Math.random()*9+1)*10000000);
            for(int j = 0;j < 2 ;j++){
                Message msg = new Message("AAA","TagA",("推送的订单ID为="+orderId+"--顺序为："+j).getBytes());
                try {
                    SendResult sendResult = producer.send(msg, new MsgQueue() ,orderId);
                    System.out.println(sendResult);
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class MsgQueue implements MessageQueueSelector{
        @Override
        public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
            Integer id = (Integer) o;
            int index = id % list.size();
            System.out.println("放到第"+list.get(index).getQueueId()+"个队列中");
            return list.get(index);
        }
    }
}