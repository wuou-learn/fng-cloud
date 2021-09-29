package com.fng.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fng.base.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 下午3:47
 * @Version 1.0.0
 */
@Data
@Entity
@TableName("user")
public class User extends BaseEntity {

    private static final long serialVersionUID = -6561042807055369617L;

    private String username;

    private String password;

    private String status;

    /**
     * 盐
     */
    private String salt;
}
