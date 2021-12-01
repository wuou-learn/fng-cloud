package com.fng.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.lang.annotation.*;

/**
 * 操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FngLog {

    /**
     * 日志类型一：记录记录实体
     * 主键
     */
    String id();

    /**
     * 操作者
     */
    String operator();

    /**
     * 日志记录
     * @return
     */
    String content();

    /**
     * 日志类型二：记录日志信息
     * 成功信息
     */
    String successMsg() default "";

    /**
     * 日志类型二：记录日志信息
     * 失败信息
     */
    String errorMsg() default "";

    /**
     * 操作日志类型
     * @return
     */
    String logType() default FngLogConstant.LOG_TYPE_MESSAGE;

    /**
     * sql类型：增删改
     */
    String sqlType() default FngLogConstant.SQL_TYPE_INSERT;

    /**
     * 业务名称
     * @return
     */
    String businessName() default "";

    /**
     * 日志类型一：记录记录实体
     * Mapper Class，需要配合 MybatisPlus 使用
     */
    Class mapperName();

}
