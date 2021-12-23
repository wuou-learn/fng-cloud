package com.fng.common;

import com.fng.exception.AppException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wuou
 * @version 1.0.0
 * @date 2020/7/31 下午3:36
 */
@Data
@Slf4j
public abstract class BaseEntity implements Serializable {

    @Id
    private String id;
    private boolean del;
    private String updateTime;
    private String createTime;

    @Transient
    private static final String SET = "set";
    @Transient
    private static final String GET = "get";

    @Transient
    public final <P> P toPo(){
        //获取当前类型对应的PO类型
        Class<?> poClass = this.getClass();
        while(BaseEntity.class.isAssignableFrom(poClass) && poClass.getAnnotation(Entity.class)==null && !poClass.equals(Object.class)){
            poClass = poClass.getSuperclass();
        }

        //如果当前对象为PO类型，及BaseEntity的直接子类，则返回当前对象
        if(this.getClass().equals(poClass)){
            return (P)this;
        }

        P po = null;
        Class<?> voClass = this.getClass();
        try {
            po = (P)poClass.newInstance();

            Method[] pms = poClass.getMethods();

            List<Method> poMethod = new ArrayList<>();
            poMethod.addAll(Arrays.asList(pms));
            for(Method m : poMethod){
                if(this.isExclude(m)){
                    continue;
                }
                //如果方法名为set方法
                String mName = m.getName();
                if(mName.indexOf(SET)==0){
                    //获取vo对应的get方法
                    String getMname = GET + mName.substring(3);
                    Method getMethod = this.method(voClass, getMname);
                    //如果get方法存在，这调用，赋值
                    if(getMethod!=null){
                        //获取vo对应属性值
                        Object val = getMethod.invoke(this);
                        //设置po对应属性值
                        m.invoke(po, val);
                    }
                }
            }
        }
        catch (InstantiationException e) {
            throw new AppException(e);
        }
        catch (IllegalAccessException e) {
            throw new AppException(e);
        }
        catch (InvocationTargetException e) {
            throw new AppException(e);
        }
        return po;
    }


    /**
     * 在VO和PO转换时需要排除的复制方法
     *
     * @param m 当前复制方法
     * @return true-排除、false-不排除
     */
    private boolean isExclude(Method m){
        if(m==null){
            return true;
        }
        List<String> names = new ArrayList<>();
        names.add("getClass");
        return names.contains(m.getName());
    }

    /**
     * 在指定类型中获取给定方法的方法引用
     *
     * @param cla 类型
     * @param name 方法名
     * @param paramTyps 参数类型
     * @return 方法引用
     */
    private Method method(Class<?> cla, String name, Class<?>...paramTyps){
        try {
            return cla.getMethod(name, paramTyps);
        }
        catch (NoSuchMethodException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 将当前对象转换为指定类型的VO对象:<br/>
     * 	仅仅支持BaseEntity的直接子类向下级转换
     *
     * @param voClass VO类型参数，必须是当前类型的字类型
     * @param <V> VO目标类型
     * @return VO对象，当前类型对象或其子类对象
     */
    @Transient
    public final <V> V toVo(Class<? extends BaseEntity> voClass){
        if(!BaseEntity.class.isAssignableFrom(this.getClass()) || this.getClass().getAnnotation(Entity.class)==null){
            throw new AppException("当前对象【"+this.getClass()+"】不是PO类型，不能向VO转换！");
        }

        Class<?> poClass = this.getClass();
        if(voClass.equals(poClass)){
            throw new AppException("目标VO类型【"+voClass+"】和当前对象类型相同，不能进行VO转换！");
        }

        //判断当前类型是否是vo类型的父类或本身
        boolean flage = false;
        Class<?> tmpClass = voClass;
        while(!tmpClass.getSuperclass().equals(Object.class)){
            if(tmpClass.equals(poClass)){
                flage = true;
                break;
            }
            tmpClass = tmpClass.getSuperclass();
        }

        V vo = null;
        if(flage){
            //目标类型是当前类型的子类型
            try {
                vo = (V)voClass.newInstance();
                Method[] pms = poClass.getMethods();
                List<Method> poMethod = new ArrayList<>();
                poMethod.addAll(Arrays.asList(pms));

                for(Method m : poMethod){
                    if(this.isExclude(m)){
                        continue;
                    }
                    //如果方法名为get方法
                    String mName = m.getName();
                    if(mName.indexOf(GET)==0){
                        //获取vo对应的set方法
                        String setMname = SET + mName.substring(3);
                        Method setMethod = this.method(voClass, setMname, m.getReturnType());
                        if(setMethod!=null){
                            //获取po对应属性值
                            Object val = m.invoke(this);
                            //设置vo对应属性值
                            setMethod.invoke(vo, val);
                        }
                    }
                }
            }
            catch (InstantiationException e) {
                throw new AppException(e);
            }
            catch (IllegalAccessException e) {
                throw new AppException(e);
            }
            catch (InvocationTargetException e) {
                throw new AppException(e);
            }
        }
        else{
            throw new AppException("当前对象类型["+poClass.getName()+"]不是["+voClass.getName()+"]的父类,不能进行VO转换!");
        }
        return vo;
    }

}
