package com.fng.common;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fng.dto.Result;
import com.fng.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author wuou
 * @version 1.0.0
 * @date 2020/7/31 下午3:38
 */
@Slf4j
public abstract class BaseController<P extends BaseEntity, V extends P, S extends ServiceImpl> {

    private ThreadLocal<V> params = new ThreadLocal<V>();
    private ThreadLocal<Result> json = new ThreadLocal<Result>();
    private ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();
    private ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
    private ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();

    private S service;

    @ModelAttribute
    public void requestInit(HttpServletRequest request, HttpServletResponse response, V item){
        this.params.set(item);
        this.request.set(request);
        this.response.set(response);
        this.session.set(request.getSession());
        this.json.set(new Result());
    }
    
    @ExceptionHandler
    public String exception(HttpServletRequest request, HttpServletResponse response, Exception e){
        String msg = null;
        Integer code = null;
        String bean = null;
        //应用异常
        if(e instanceof AppException){
            msg = e.getMessage();
            code = ((AppException) e).getCode();
        }
        else if (e instanceof TokenExpiredException){
            msg = "token已经过期,请重新登录";
            code = -1;
        }
        else if (e instanceof BlockException){
            msg = "服务器忙,请稍后再试";
            code = -1;
        }
        else if (e instanceof JsonParseException || e instanceof JsonMappingException || e instanceof IOException) {
            msg = "啊哦，服务器出小差咯，请联系管理员";
            bean = e.getMessage();
            code = -2;
            log.error(e.getMessage());
        }
        //非自定义异常
        else{
            msg = "啊哦，服务器出小差咯，请联系管理员";
            bean = e.getMessage();
            code = -3;
            log.error("--------LogException-------->:", e);
        }

        //异常结果处理
        return this.exceptionResponse(request, response, code, msg, bean);
    }

    protected String exceptionResponse(HttpServletRequest request, HttpServletResponse response, Integer code, String msg, Object obj) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().print("{\"message\":\""+msg+"\",\"code\":"+code+"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void exportExcelResponse(String excelName){
        HttpServletResponse response = this.getResponse().get();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename="+excelName+".xlsx");

    }

    public Result json(){
        return this.json.get();
    }

    public S service(){
        return service;
    }

    public V params() {
        return params.get();
    }

    /**
     * 获取 params
     * @return the params
     */
    public ThreadLocal<V> getParams() {
        return params;
    }

    /**
     * 设置 params
     * @param params the params to set
     */
    public void setParams(ThreadLocal<V> params) {
        this.params = params;
    }

    /**
     * 获取 service
     * @return the service
     */
    public S getService() {
        return service;
    }

    /**
     * 设置 service
     * @param service the service to set
     */
    @Resource
    public void setService(S service) {
        this.service = service;
    }

    /**
     * 获取 request
     * @return the request
     */
    public ThreadLocal<HttpServletRequest> getRequest() {
        return request;
    }

    /**
     * 设置 request
     * @param request the request to set
     */
    public void setRequest(ThreadLocal<HttpServletRequest> request) {
        this.request = request;
    }

    /**
     * 获取 response
     * @return the response
     */
    public ThreadLocal<HttpServletResponse> getResponse() {
        return response;
    }

    /**
     * 设置 response
     * @param response the response to set
     */
    public void setResponse(ThreadLocal<HttpServletResponse> response) {
        this.response = response;
    }

    /**
     * 获取 session
     *
     * @return the session
     */
    public ThreadLocal<HttpSession> getSession() {
        return session;
    }

    /**
     * 设置 session
     *
     * @param session the session to set
     */
    public void setSession(ThreadLocal<HttpSession> session) {
        this.session = session;
    }

    /**
     * 获取 json
     *
     * @return the json
     */
    public ThreadLocal<Result> getJson() {
        return json;
    }

    /**
     * 设置 json
     *
     * @param json the json to set
     */
    public void setJson(ThreadLocal<Result> json) {
        this.json = json;
    }
}
