package com.fng.web.user;

import com.fng.dto.Result;
import com.fng.pojo.po.User;
import com.fng.pojo.vo.UserVo;
import com.fng.service.UserService;
import com.fng.utils.EncryptComponentException;
import com.fng.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 下午3:45
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController<User, UserVo, UserService> {

    /**
     * 登录接口
     * @return
     * @throws EncryptComponentException 加解密异常
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login() throws EncryptComponentException {
        this.json().setData(this.service().login(this.params()));
        return this.json();
    }

    /**
     * 注册接口
     * @return
     * @throws EncryptComponentException 加解密异常
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Result add() throws EncryptComponentException {
        this.service().add(this.params());
        this.json().setMessage("新增成功");
        return this.json();
    }

}
