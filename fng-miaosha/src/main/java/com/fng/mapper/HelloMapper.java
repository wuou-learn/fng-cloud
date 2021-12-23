package com.fng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fng.entity.Hello;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/6 上午11:57
 * @Version 1.0.0
 */
@Mapper
public interface HelloMapper extends BaseMapper<Hello> {
}
