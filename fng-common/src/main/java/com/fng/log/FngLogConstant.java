package com.fng.log;


public interface FngLogConstant {

    /**
     * 错误信息正则
     */
    String ERROR_MSG_PATTERN = "{{#_errorMsg}}";

    Class EMPTY = null;

    /**
     * 操作日志记录类型：信息
     */
    String LOG_TYPE_MESSAGE = "message";
    /**
     * 操作日志记录类型：实体
     */
    String LOG_TYPE_RECORD = "record";
    /**
     * SQL类型：新增
     */
    String SQL_TYPE_INSERT = "insert";
    /**
     * SQL类型：更新
     */
    String SQL_TYPE_UPDATE = "update";
    /**
     * SQL类型：删除
     */
    String SQL_TYPE_DELETE = "delete";
    /**
     * SQL类型：查询
     */
    String SQL_TYPE_QUERY = "query";
}
