package com.fng.log;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/1 下午2:51
 * @Version 1.0.0
 */
@Aspect
@Slf4j
@Component
public class FngLogAspect {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Autowired
    ApplicationContext applicationContext;

    @Around("@annotation(fngLog)")
    public Object around(final ProceedingJoinPoint point, final FngLog fngLog) throws Throwable {
        final Method method = this.getMethod(point);
        final Object[] args = point.getArgs();
        final FngLogRecord record = new FngLogRecord();

        // 日志类型
        final String logType = fngLog.logType();
        // sql类型
        final String sqlType = fngLog.sqlType();
        // 业务名称
        final String businessName = fngLog.businessName();

        final EvaluationContext context = this.bindParam(method, args);

        // 获取操作者
        final String operator = this.getOperator(fngLog.operator(), context);

        record.setLogType(logType);
        record.setSqlType(sqlType);
        record.setBusinessName(businessName);
        record.setOperator(operator);

        Object proceedResult = null;

        if (FngLogConstant.LOG_TYPE_RECORD.equals(logType)){
            final Class mapperClass = fngLog.mapperName();
            if (mapperClass.isAssignableFrom(BaseMapper.class)){
                throw new RuntimeException("mapperClass 属性传入 Class 不是 BaseMapper 的子类");
            }
            final BaseMapper mapper = (BaseMapper) this.applicationContext.getBean(mapperClass);
            //根据spel表达式获取id
            final String id = (String) this.getId(fngLog.id(), context);
            final Object beforeRecord;
            final Object afterRecord;
            switch (sqlType){
                // 新增
                case FngLogConstant.SQL_TYPE_INSERT:
                    proceedResult = point.proceed();
                    record.setBeforeRecord("");
                    record.setAfterRecord(JSON.toJSONString(proceedResult));
                    break;
                // 更新
                case FngLogConstant.SQL_TYPE_UPDATE:
                    beforeRecord = mapper.selectById(id);
                    proceedResult = point.proceed();
                    afterRecord = mapper.selectById(id);
                    record.setBeforeRecord(JSON.toJSONString(beforeRecord));
                    record.setAfterRecord(JSON.toJSONString(afterRecord));
                    break;
                default:
                    break;
            }
        }

        return proceedResult;
    }

    /**
     * 获取当前执行的方法
     *
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(final ProceedingJoinPoint pjp) throws NoSuchMethodException {
        final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        final Method method = methodSignature.getMethod();
        final Method targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        return targetMethod;
    }

    /**
     * 将方法的参数名和参数值绑定
     *
     * @param method 方法，根据方法获取参数名
     * @param args   方法的参数值
     * @return
     */
    private EvaluationContext bindParam(final Method method, final Object[] args) {
        //获取方法的参数名
        final String[] params = this.discoverer.getParameterNames(method);

        //将参数名与参数值对应起来
        final EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        return context;
    }

    /**
     * 获取操作者
     * @param expressionStr
     * @param context
     * @return
     */
    private String getOperator(final String expressionStr, final EvaluationContext context){
        try {
            if (expressionStr.startsWith("#")){
                final Expression idExpression = this.parser.parseExpression(expressionStr);
                return (String) idExpression.getValue(context);
            }else {
                return expressionStr;
            }
        }catch (final Exception e){
            log.error("Log-Record-SDK 获取操作者失败！，错误信息：{}",e.getMessage());
            return "default";
        }
    }

    /**
     * 根据表达式获取ID
     * @param expressionStr
     * @param context
     * @return
     */
    private Object getId(final String expressionStr, final EvaluationContext context){
        final Expression idExpression = this.parser.parseExpression(expressionStr);
        return idExpression.getValue(context);
    }
}
