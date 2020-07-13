package com.onchain.controller;

import com.onchain.bean.ResponseFormat;
import com.onchain.bean.dto.User;
import com.onchain.bean.requestbean.RequestAuthorize;
import com.onchain.bean.requestbean.RequestLogin;
import com.onchain.bean.requestbean.RequestRefresh;
import com.onchain.bean.requestbean.RequestRegister;
import com.onchain.bean.responsebean.DoubleToken;
import com.onchain.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname JwtController
 * @Description TODO
 * @Date 2020/7/10 16:44
 * @Created by zhaochen
 */
@RestController
@RequestMapping(value = "/")
public class JwtController {

    @Autowired
    private JwtService jwtService;

    /**
     * 根据手机号获取用户信息
     */
    @GetMapping("/getUserByPhoneNumber")
    public ResponseFormat getUserByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        ResponseFormat<User> rsp = new ResponseFormat<>(2000);
        User user = jwtService.getUserByPhoneNumber(phoneNumber);
        rsp.setData(user);
        return rsp;
    }

    /**
     * 注册新用户
     */
    @PostMapping("/register")
    public ResponseFormat register(@RequestBody RequestRegister requestRegister) {
        ResponseFormat<Boolean> rsp = new ResponseFormat<>(2000);
        Boolean ret = jwtService.register(requestRegister);
        rsp.setData(ret);
        return rsp;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseFormat login(@RequestBody RequestLogin requestLogin) {
        ResponseFormat<DoubleToken> rsp = new ResponseFormat<>(2000);
        DoubleToken doubleToken = jwtService.login(requestLogin);
        rsp.setData(doubleToken);
        return rsp;
    }

    /**
     * 校验请求头中的accessToken
     */
    @PostMapping("/checkAccess")
    public ResponseFormat checkAccess(HttpServletRequest req) {
        ResponseFormat<Boolean> rsp = new ResponseFormat<>(2000);
        String accessToken = req.getHeader("accessToken");
        Boolean b = jwtService.checkAccess(accessToken);
        rsp.setData(b);
        return rsp;
    }

    /**
     * 刷新accessToken
     */
    @PostMapping("/refresh")
    public ResponseFormat refresh(@RequestBody RequestRefresh requestRefresh) {
        ResponseFormat<String> rsp = new ResponseFormat<>(2000);
        String accessToken = jwtService.refresh(requestRefresh);
        rsp.setData(accessToken);
        return rsp;
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseFormat logout(@RequestParam("userId") String userId) {
        ResponseFormat<Boolean> rsp = new ResponseFormat<>(2000);
        Boolean ret = jwtService.logout(userId);
        rsp.setData(ret);
        return rsp;
    }

    /**
     * 授予用户权限
     */
    @PostMapping("/authorize")
    public ResponseFormat authorize(@RequestBody RequestAuthorize requestAuthorize) {
        ResponseFormat<Boolean> rsp = new ResponseFormat<>(2000);
        Boolean ret = jwtService.authorize(requestAuthorize);
        rsp.setData(ret);
        return rsp;
    }
}
