package com.fng.common;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoundLoaderBalancer implements LoadBalance {

    private static AtomicInteger idx = new AtomicInteger(0);
    private static List<LoadBService> serviceList = new ArrayList<>();
    {
        LoadBService service0 = new LoadBService("service0","miaoshaKey1");
        LoadBService service1 = new LoadBService("service1","miaoshaKey2");
        LoadBService service2 = new LoadBService("service2","miaoshaKey3");
        LoadBService service3 = new LoadBService("service3","miaoshaKey4");
        LoadBService service4 = new LoadBService("service4","miaoshaKey5");
        serviceList.add(service0);
        serviceList.add(service1);
        serviceList.add(service2);
        serviceList.add(service3);
        serviceList.add(service4);
    }
    @Override
    public LoadBService doPickOneService() {
        int index = getNextNonNegative();
        for (int i = 0; i < serviceList.size(); i++) {
            //获取服务
            LoadBService service = serviceList.get((i + index) % serviceList.size());
            //判断服务是否可用
            if (service.isAvailable()) {
                return service;
            }
        }
        return null;
    }

    /**
     * 假设有这么一个获取全部服务的列表
     *
     * @return
     */
    public List<LoadBService> getServiceList() {
        List<LoadBService> serviceList = new ArrayList<>();
        LoadBService service0 = new LoadBService("service0","miaoshaKey1");
        LoadBService service1 = new LoadBService("service1","miaoshaKey2");
        LoadBService service2 = new LoadBService("service2","miaoshaKey3");
        LoadBService service3 = new LoadBService("service3","miaoshaKey4");
        LoadBService service4 = new LoadBService("service4","miaoshaKey5");
        serviceList.add(service0);
        serviceList.add(service1);
        serviceList.add(service2);
        serviceList.add(service3);
        serviceList.add(service4);
        return serviceList;
    }

    private int getNextNonNegative() {
        return getNonNegative(idx.incrementAndGet());
    }

    /**
     * 通过二进制位操作将originValue转化为非负数:
     * 0和正数返回本身
     * 负数通过二进制首位取反转化为正数或0（Integer.MIN_VALUE将转换为0）
     * return non-negative int value of originValue
     *
     * @param originValue
     * @return positive int
     */
    public int getNonNegative(int originValue) {
        return 0x7fffffff & originValue;
    }

    public static void main(String[] args) {
        RoundLoaderBalancer roundLoaderBalancer = new RoundLoaderBalancer();
        for(int i=0; i<10; i++){
            LoadBService loadBService = roundLoaderBalancer.doPickOneService();
            System.out.println(loadBService);
        }
    }

}
