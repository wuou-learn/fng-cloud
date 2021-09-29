package com.fng.redis;

import com.fng.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
public abstract class RedisUtilAbstract {

	@Autowired
	private RedisTemplate redisTemplate;

	public boolean exists(final String key) {
		return this.redisTemplate.hasKey(key);
	}
	
	   /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key,long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	/**
	 * 批量删除对应的value
	 * @param keys 要删除的key集合
	 */
	public void remove(final String... keys) {
		for(String key : keys) {
			this.remove(key);
		}
	}

	/**
	 * 删除对应的value
	 * @param key 要删除的键
	 */
	@SuppressWarnings("unchecked")
	public void remove(final String key) {
		if (exists(key)) {
			this.redisTemplate.delete(key);
		}
	}


	/**
	 * 写入value--string类型的值
	 * @param key 键
	 * @param value 值
	 * @return 写入是否成功
	 */
	@SuppressWarnings("unchecked")
	public boolean set(final String key, Object value) {
		ValueOperations<Serializable, Object> opt = this.redisTemplate.opsForValue();
		opt.set(key, value);
		return true;
	}


	/**
	 * 写入value--string类型的值
	 * @param key 键
	 * @param value 值
	 * @param time 过期时间-秒
	 * @return 写入是否成功
	 */
	@SuppressWarnings("unchecked")
	public boolean set(final String key, Object value, long time) {
		ValueOperations<Serializable, Object> opt = this.redisTemplate.opsForValue();
		opt.set(key, value, time, TimeUnit.SECONDS);
		return true;
	}

	/**
	 * 读取value--string类型的值
	 * @param <X> 需要获取的对象类型
	 * @param key 键
	 * @return 读取的对象
	 */
	@SuppressWarnings("unchecked")
	public <X> X get(final String key) {
		ValueOperations<Serializable, Object> opt = this.redisTemplate.opsForValue();
		return (X)opt.get(key);
	}


	/**
	 * 读取并删除List中的左边第一个value
	 * @param <X> 需要获取的对象类型
	 * @param key 键
	 * @return 值
	 */
	@SuppressWarnings("unchecked")
	public <X> X lLeftGet(final String key){
		ListOperations<String,Object> opt = this.redisTemplate.opsForList();
		return (X)opt.leftPop(key);
	}

	/**
	 * 在List的左边加入数据
	 * @param key 键
	 * @param lvalue 值
	 * @return 写入是否成功
	 */
	@SuppressWarnings("unchecked")
	public boolean lLeftSet(final String key, Object lvalue){
		ListOperations<String, Object> opt = this.redisTemplate.opsForList();
		opt.leftPush(key, lvalue);
		return true;
	}

	/**
	 * 获取list中的元素个数
	 * @param key 键
	 * @return value数
	 */
	@SuppressWarnings("unchecked")
	public Long lgetSzie(final String key){
		ListOperations<String,Object> opt = this.redisTemplate.opsForList();
		return opt.size(key);
	}

	/**
	 * 自增
	 * @param key
	 * @param delta
	 * @return
	 */
	public long incr(String key, Integer delta){
		if(delta<0){
			throw new AppException("递增因子必须大于0");
		}
		return this.redisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 自减
	 * @param key
	 * @param delta
	 * @return
	 */
	public long decr(String key, long delta){
		if(delta<0){
			throw new AppException("递增因子必须大于0");
		}
		return this.redisTemplate.opsForValue().decrement(key, delta);
	}

	/**
	 * 设置过期时间
	 * @param key
	 * @param value
	 * @param time
	 */
	public void setUpByTime(String key,String value,Long time){
		redisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
	}

	/**
	 * 读取map所有数据
	 * @param key 键
	 * @return 返回集合
	 */
	@SuppressWarnings("unchecked")
	public List<?> listRange(final String key){
		ListOperations<String,Object> opt = this.redisTemplate.opsForList();
		return opt.range(key, 0, -1);
	}

	public Map hentries(String key) {
		HashOperations<String, String, String> operations = redisTemplate.opsForHash();
		return operations.entries(key);
	}

	@Autowired(required = false)
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		RedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(RedisSerializer.json());
		redisTemplate.setHashKeySerializer(RedisSerializer.json());
		redisTemplate.setHashValueSerializer(RedisSerializer.json());
		this.redisTemplate = redisTemplate;
	}
	
	
    //=================================list======================================
    public long leftPush(String key, String value) {
    	return redisTemplate.opsForList().leftPush(key,value); 
    }
    public long leftPushAll(String key, String ...values) {
    	return redisTemplate.opsForList().leftPushAll(key,values); 
    }
    
    public Object rightPop(String key) {
    	return redisTemplate.opsForList().rightPop(key);  
    }
    
    public long listSize(String key) {
    	return redisTemplate.opsForList().size(key);    
    }
    
    
    //===========================set =========================
    
    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
}
