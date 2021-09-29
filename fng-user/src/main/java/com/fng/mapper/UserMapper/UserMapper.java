package com.fng.mapper.UserMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fng.pojo.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 下午4:46
 * @Version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
