package com.fng.controller;

import com.fng.common.BaseController;
import com.fng.common.LoadBService;
import com.fng.common.RoundLoaderBalancer;
import com.fng.dto.Result;
import com.fng.entity.Hello;
import com.fng.entity.HelloVo;
import com.fng.exception.AppException;
import com.fng.redis.RedisUtil;
import com.fng.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/6 上午10:41
 * @Version 1.0.0
 */
@RestController
@Slf4j
@RequestMapping("/miaosha")
public class HelloController extends BaseController<Hello, HelloVo, HelloService> {

    @Autowired
    private RedisUtil redis;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RoundLoaderBalancer roundLoaderBalancer;


    @Value("${server.port}")
    private String port;

    @RequestMapping("/hello")
    public synchronized Result hello() throws InterruptedException {
        Integer miaoshaKey = redis.get("miaoshaKey");
        if(miaoshaKey >= 1000){
            throw new AppException("商品库存不足.");
        }
        TimeUnit.MILLISECONDS.sleep(100);
        long miaoshaKey1 = redis.incr("miaoshaKey", 1);
        this.json().setData(miaoshaKey1);
        this.json().setMessage("port:"+port);
        return this.json();
    }

    @RequestMapping("/redisson")
    public Result redisson() throws InterruptedException {
        RLock lock = redissonClient.getLock("hongbao-key");
        lock.lock();
        long miaoshaKey1 = 0;
        try {
            Integer miaoshaKey = redis.get("miaoshaKey");
            if(miaoshaKey >= 1000){
                throw new AppException("商品库存不足.");
            }
            TimeUnit.MILLISECONDS.sleep(50);
            miaoshaKey1 = redis.incr("miaoshaKey", 1);
        } finally {
            lock.unlock();
        }
        this.json().setData(miaoshaKey1);
        this.json().setMessage("port:"+port);
        return this.json();
    }

    @RequestMapping("/real")
    public Result real() throws InterruptedException {
        RLock lock = redissonClient.getLock("hongbao-key");
        lock.lock();
        long miaoshaKey1 = 0;
        try {
            Integer miaoshaKey = redis.get("miaoshaKey");
            if(miaoshaKey <= 0){
                throw new AppException("商品库存不足.");
            }
            TimeUnit.MILLISECONDS.sleep(18);
            miaoshaKey1 = redis.decr("miaoshaKey", 1);
        } finally {
            lock.unlock();
        }
        this.json().setData(miaoshaKey1);
        this.json().setMessage("port:"+port);
        return this.json();
    }


    @RequestMapping("/divideReal")
    public Result divideReal() throws InterruptedException {
        RLock lock = null;
        boolean flag = false;
        long miaoshaKey1 = 0;
        try {
            while(!flag){
                LoadBService loadBService = roundLoaderBalancer.doPickOneService();
                if(loadBService == null){
                    throw new AppException("库存不足.");
                }
                lock = redissonClient.getLock(loadBService.getName());
                flag = lock.tryLock(5,TimeUnit.MILLISECONDS);
                if(loadBService.getSum().get() <= 0){
                    throw new AppException("库存不足.");
                }
                if((Integer) redis.get(loadBService.getKey()) <= 0){
                    throw new AppException("库存不足.");
                }
                if(flag){
                    log.info("线程拿到锁-->"+Thread.currentThread().getName());
                    log.info("消费开始端口："+port+"-->"+loadBService.toString());
                    TimeUnit.MILLISECONDS.sleep(100);
                    miaoshaKey1 = redis.decr(loadBService.getKey(), 1);
                    loadBService.getSum().getAndDecrement();
                    if(loadBService.getSum().get() <= 0 || miaoshaKey1 <= 0){
                        loadBService.setAvailable(false);
                    }
                }
            }
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()){
                // 处理业务逻辑
                // 解锁
                if (lock.isLocked()){
                    lock.unlock();
                }
                if (!lock.isLocked()){
                    log.info("线程解锁-->"+Thread.currentThread().getName()+"解锁成功");
                }
            }
        }
        this.json().setData(miaoshaKey1);
        this.json().setMessage("port:"+port);
        return this.json();
    }
}
