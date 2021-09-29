package com.fng.pojo.vo;

import com.fng.pojo.po.User;
import lombok.Data;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 下午3:47
 * @Version 1.0.0
 */
@Data
public class UserVo extends User {

    private String token;

}
