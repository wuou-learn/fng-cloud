package com.fng.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fng.exception.AppException;
import com.fng.log.FngLog;
import com.fng.log.FngLogConstant;
import com.fng.mapper.UserMapper.UserMapper;
import com.fng.pojo.po.User;
import com.fng.pojo.vo.UserVo;
import com.fng.redis.RedisUtil;
import com.fng.uitils.JwtUtil;
import com.fng.uitils.UUIDUtil;
import com.fng.utils.AESUtils;
import com.fng.utils.EncryptComponentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 下午4:43
 * @Version 1.0.0
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private RedisUtil redis;

    /**
     * 1. 根据token登录
     * 2. token失效 账号密码登录
     * @param params
     */
    public Object login(UserVo params) throws EncryptComponentException {
        // 有token 验证token
        if(StrUtil.isNotBlank(params.getToken())){
            String id = JwtUtil.verifyToken(params.getToken());
            User user = this.baseMapper.selectById(id);
            user.setPassword(null);
            user.setSalt(null);
            return user;
        }
        // 没有token校验 用户名密码
        judgeNameAndPassword(params);

        // 根据用户名查询 用户
        User user = selectByUsername(params);

        if(null == user){
            throw new AppException("您输入的用户名密码错误");
        }

        // 校验密码
        judgePassword(params,user);

        // 生成token返回前端
        String token = JwtUtil.createToken(user.getId());
        UserVo res = user.toVo(UserVo.class);
        res.setPassword(null);
        res.setSalt(null);
        res.setToken(token);
        redis.setUpByTime("user:token-"+token, res, (long) 99999);
        return res;
    }

    private User selectByUsername(UserVo params) {
        return this.baseMapper.selectOne(
                new QueryWrapper<User>().eq("username", params.getUsername()));
    }

    /**
     * 校验密码
     * @param params
     * @param user
     */
    private void judgePassword(UserVo params, User user) throws EncryptComponentException {
        if(!StrUtil.equals(user.getPassword(),
                AESUtils.encrypt(params.getPassword(),user.getSalt(),AESUtils.AES256))){
            throw new AppException("您输入的用户名密码错误");
        }
    }

    /**
     * 校验用户名密码
     * @param params
     */
    private void judgeNameAndPassword(UserVo params) {
        if(StrUtil.isBlank(params.getUsername())){
            throw new AppException("请输入用户名");
        }

        if(StrUtil.isBlank(params.getPassword())){
            throw new AppException("请输入密码");
        }
    }

    /**
     * 添加用户
     * @param params
     * @return
     */
    @FngLog(logType = FngLogConstant.LOG_TYPE_RECORD,
            sqlType = FngLogConstant.SQL_TYPE_INSERT,
            businessName = "用户服务",
            content = "新增用户",
            operator = "#params.username",
            mapperName = UserMapper.class,
            id = "#params.id")
    public UserVo add(UserVo params) throws EncryptComponentException {
        judgeNameAndPassword(params);

        User user = selectByUsername(params);

        if(null != user){
            throw new AppException("该用户名已存在");
        }

        // 生成ID
        params.setId(UUIDUtil.getUUID());
        String salt = UUIDUtil.getRandomString(12);
        params.setPassword(AESUtils.encrypt(
                params.getPassword(),salt,AESUtils.AES256));
        params.setSalt(salt);

        this.baseMapper.insert(params);

        return params;
    }

}
