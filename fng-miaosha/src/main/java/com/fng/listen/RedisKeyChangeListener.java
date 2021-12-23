package com.fng.listen;

import java.nio.charset.Charset;
import java.util.Properties;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.listener.KeyspaceEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.util.StringUtils;

/**
 * 
 * @author hy
 * @createTime 2021-05-01 08:53:19
 * @description 期望是可以监听某个key的变化，而不是失效
 *
 */
public class RedisKeyChangeListener implements MessageListener/* extends KeyspaceEventMessageListener */ {
	private final String listenerKeyName; // 监听的key的名称
	private static final Topic TOPIC_ALL_KEYEVENTS = new PatternTopic("__keyevent@*"); //表示只监听所有的key 
	private static final Topic TOPIC_KEYEVENTS_SET = new PatternTopic("__keyevent@0__:set"); //表示只监听所有的key
	private static final Topic TOPIC_KEYNAMESPACE_NAME = new PatternTopic("__keyspace@0__:myKey"); // 不生效
	// 监控
	//private static final Topic TOPIC_KEYEVENTS_NAME_SET_USELESS = new PatternTopic("__keyevent@0__:set myKey");
	private String keyspaceNotificationsConfigParameter = "KEA";
	public RedisKeyChangeListener(RedisMessageListenerContainer listenerContainer, String listenerKeyName) {
		this.listenerKeyName = listenerKeyName;
		initAndSetRedisConfig(listenerContainer);
	}

	public void initAndSetRedisConfig(RedisMessageListenerContainer listenerContainer) {

		if (StringUtils.hasText(keyspaceNotificationsConfigParameter)) {

			RedisConnection connection = listenerContainer.getConnectionFactory().getConnection();

			try {

				Properties config = connection.getConfig("notify-keyspace-events");

				if (!StringUtils.hasText(config.getProperty("notify-keyspace-events"))) {
					connection.setConfig("notify-keyspace-events", keyspaceNotificationsConfigParameter);
				}

			} finally {
				connection.close();
			}
		}
		// 注册消息监听
		listenerContainer.addMessageListener(this, TOPIC_KEYNAMESPACE_NAME);
	}



	@Override
	public void onMessage(Message message, byte[] pattern) {
		System.out.println("key发生变化===》" + message);
		byte[] body = message.getBody();
		String string = new String(body, Charset.forName("utf-8"));
		System.out.println(string);

	}

}