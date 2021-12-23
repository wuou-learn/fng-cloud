package com.fng.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @version 1.0.0
 * @@menu <p>
 * @date 2021/6/10 14:33
 */
public class SegmentDistributeLock {
    /**
     *  使用redis分布式锁扣减库存,弊端: 请求量大的话,会导致吞吐量降低
     *  优化: 分段锁并发扣减库存
     *      将表中的库存字段 分为 5个库存字段, 然后导入redis,库存预热, 然后参考ConcurrentHashMap的分段锁思想
     *      来一个请求后,对库存字段 加 分段锁, 分段锁扣减库存
     *      如果当前分段锁库存不够,就扣减掉当前的库存,然后去锁下一个分段锁,扣减库存
     *
     *      git: https://gitee.com/easybao/segmentDistributeLock.git
     *      依赖jar包:
     *       <dependency>
     *             <groupId>org.redisson</groupId>
     *             <artifactId>redisson</artifactId>
     *             <version>3.13.5</version>
     *         </dependency>
     */
    RedissonClient redissonClient;
    RBucket<RedisStock[]> bucket;
    private ThreadLocal<StockRequest> threadLocal = new ThreadLocal<>();
    static volatile RedisStock[] redisStocks;
    private final int beginTotalNum; //初始总库存,避免并发过程中 调用getCurrentTotalNum()获取到的总库存发生变化

    public SegmentDistributeLock()  {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.useSingleServer().setPassword("Thatyear15");
        this.redissonClient = Redisson.create(config);

        redisStocks = new RedisStock[5];
        redisStocks[0] = new RedisStock("pId_stock_00",20);
        redisStocks[1] = new RedisStock("pId_stock_01",20);
        redisStocks[2] = new RedisStock("pId_stock_02",20);
        redisStocks[3] = new RedisStock("pId_stock_03",20);
        redisStocks[4] = new RedisStock("pId_stock_04",20);
        // 初始总库存
        this.beginTotalNum = getCurrentTotalNum();

        // 库存预热,存到redis中 ,  这里没有采用因为将库存预热存到redis中,取出来的时候,解析异常, 不想花时间解决,所以将库存预热 变成一个类变量
//        bucket = redissonClient.getBucket("pId_stock");
//        bucket.set(redisStocks);

    }
    public RedissonClient getRedissonClient(){
        return this.redissonClient;
    }

    public int getCurrentTotalNum(){
        // 获取实时总库存
        return Stream.of(redisStocks).mapToInt(RedisStock::getNum).sum();
    }


    /**
     *  使用redis分布式锁扣减库存,弊端: 请求量大的话,会导致吞吐量降低
     *  优化: 分段锁并发扣减库存
     *      将表中的库存字段 分为 5个库存字段, 然后导入redis,库存预热, 然后参考ConcurrentHashMap的分段锁思想
     *      来一个请求后,对库存字段 加 分段锁, 分段锁扣减库存
     *      如果当前分段锁库存不够,就扣减掉当前的库存,然后去锁下一个分段锁,扣减库存
     * @param request
     * @return
     */
    public boolean handlerStock_02(StockRequest request) {
        // 先做校验: 判断扣减库存 是否比 初始总库存还大,是的话就直接false,  避免无限循环扣减不了
        if(request.getBuyNum() > this.beginTotalNum){
            return false;
        }
        // 使用本地线程变量保存请求,确保参数只在本线程使用
        threadLocal.set(request);

        // 这里使用 ThreadLocal代码逻辑和ConcurrentHashMap的分段锁
        RedissonClient redissonClient = getRedissonClient();
        RedisStock[] tab = redisStocks;
        int len = tab.length;
        int i = (request.getMemberId().hashCode() < 0 ? 0 : request.getMemberId().hashCode() ) % len ;

        for(RedisStock e = tab[i]; e != null; e = tab[i = nextIndex(i,len)]){

            RLock segmentLock = null;
            try {
                // 2: 对该元素加分布式分段锁
                segmentLock = redissonClient.getLock(e.getStockName());
                segmentLock.lock();

                int buyNum = threadLocal.get().getBuyNum();
                if (buyNum <= e.getNum()) {
                    //扣减库存
                    e.setNum(e.getNum() - buyNum);
                    // 扣减成功后,跳出循环,返回结果
                    return true;
                }else{
                    // 如果并发过程中获取到总库存<= 0 说明已经没有库存了,  如果当前需要扣减的库存 > 此时总库存就返回false,扣件失败
                    if (getCurrentTotalNum() <= 0 || threadLocal.get().getBuyNum() > getCurrentTotalNum()) {
                        // 没有库存就false
                        System.out.println(Thread.currentThread().getName() + " 扣减库存数: " + threadLocal.get().getBuyNum() + "失败" + "   此时总库存为: " + getCurrentTotalNum());
                        return false;
                    }
                    // 扣减掉当前的 分段锁对应的库存,然后对下一个元素加锁
                    threadLocal.get().setBuyNum( buyNum - e.getNum());
                    e.setNum(0);
                }
            } finally {
                // 3: 解锁
                segmentLock.unlock();
            }
        }
        threadLocal.remove();
        return false;
    }

    private static int nextIndex(int i, int len) {
        return ((i + 1 < len) ? i + 1 : 0);
    }
    public static int FNVHash(String key) {
        final int p = 16777619;
        Long hash = 2166136261L;
        for (int idx = 0, num = key.length(); idx < num; ++idx) {
            hash = (hash ^ key.charAt(idx)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash.intValue();
    }

    // 显示redis中的库存
    public void showStocks(){
        for (RedisStock redisStock : redisStocks) {
            System.out.println(redisStock);
        }
    }

    @AllArgsConstructor
    class RedisStock implements Serializable {
        // 库存字段
        String stockName;
        // 库存数据, 原子类来保证原子性 num的原子性
        AtomicInteger num;

        public RedisStock(String stockName, int num) {
            this.stockName = stockName;
            this.num = new AtomicInteger(num);
        }

        public void setNum(int num) {
            this.num.set(num);
        }

        public String getStockName() {
            return stockName;
        }

        public void setStockName(String stockName) {
            this.stockName = stockName;
        }

        public int getNum() {
            return this.num.get();
        }

        @Override
        public String toString() {
            return "RedisStock{" +
                    "stockName='" + stockName + '\'' +
                    ", num=" + num.get() +
                    '}';
        }
    }
}
@Getter
@Setter
@AllArgsConstructor
class StockRequest implements Serializable{
    //会员id
    String memberId;
    //购买数量
    int buyNum;
}

class SegmentDistributeLockTest{
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 模拟单线程扣减
        SegmentDistributeLock segmentDistributeLock = new SegmentDistributeLock();
        if(segmentDistributeLock.handlerStock_02(new StockRequest("memberId_001",54))){
            System.out.println("扣减成功");
        }else{
            System.out.println("扣减失败");
        }
        segmentDistributeLock.showStocks();
        /**
         * 成功; 结果为:
         * RedisStock{stockName='pId_stock_00', num=0}  扣减了20个
         * RedisStock{stockName='pId_stock_01', num=10} 扣减了10个
         * RedisStock{stockName='pId_stock_02', num=20}
         * RedisStock{stockName='pId_stock_03', num=20}
         * RedisStock{stockName='pId_stock_04', num=20}
         */
    }
}

class ConcurrentTest implements Runnable{
    // 模拟10个线程并发
    private static CountDownLatch countDownLatch = new CountDownLatch(10);
    private static SegmentDistributeLock segmentDistributeLock = new SegmentDistributeLock();
    int num; //购买数量
    //会员id
    String memberId;

    public ConcurrentTest(String memberId,int num) {
        this.num = num;
        this.memberId = memberId;
    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        // 模拟并发扣减库存(扣减1-50个)
        for (int i = 0; i < 10; i++) {
            new Thread(new ConcurrentTest("memberId_"+i,random.nextInt(50) + 1),"线程"+i).start();
            countDownLatch.countDown();
        }
        TimeUnit.SECONDS.sleep(5);
        // 并发扣减库存结束,查询最终库存
        System.out.println("-----并发扣减库存结束,查看剩余库存-------");
        System.out.println("-----并发扣减库存结束,查看剩余库存-------");
        System.out.println("-----并发扣减库存结束,查看剩余库存-------");
        segmentDistributeLock.showStocks();
    }
    @Override
    public void run() {
        try {
            StockRequest request = new StockRequest(this.memberId, this.num);
            // 在此阻塞,等到计数器归零之后,再同时开始 扣库存
            System.out.println(Thread.currentThread().getName() + "已到达, 即将开始扣减库存: "+ this.num);
            countDownLatch.await();
            if(segmentDistributeLock.handlerStock_02(request)){
                System.out.println(Thread.currentThread().getName() + " 扣减成功, 扣减库存为: " + this.num);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}