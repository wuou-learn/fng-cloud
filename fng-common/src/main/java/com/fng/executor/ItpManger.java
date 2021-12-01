package com.fng.executor;

import com.fng.exception.AppException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池类
 */
public class ItpManger{

	// 缓存线程池
	private static final Map<String, ExecutorService> pools = new HashMap<String, ExecutorService>();

    /**
     * 像线程池中添加任务
     */
    public static synchronized void add(SendMessageRunnable task, String poolType){
    	if(null == task){
        	throw new AppException("不能添加空任务。");
        }
        ExecutorService pool = getPool(poolType);
        pool.execute(task);
    }

	/**
     * 根据type类型得到线程池
     */
	public static synchronized ExecutorService getPool(String poolType){
    	ExecutorService pool = pools.get(poolType);
    	if(null == pool){
        	if(poolType.equals("FixedPool")){
            	pool = Executors.newFixedThreadPool(10);
            }
        	pools.put(poolType,pool);
        }
    	return pool;
    }
}