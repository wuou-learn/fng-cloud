package com.fng.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fng.entity.Hello;
import com.fng.mapper.HelloMapper;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/6 上午11:53
 * @Version 1.0.0
 */
@Service
public class HelloService extends ServiceImpl<HelloMapper, Hello> {
}
