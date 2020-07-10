package com.onchain.controller;

import com.onchain.bean.dto.User;
import com.onchain.bean.requestbean.RequestLogin;
import com.onchain.bean.requestbean.RequestRegister;
import com.onchain.bean.responsebean.DoubleToken;
import com.onchain.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    @GetMapping("/getUserByPhoneNumber")
    public User getUserByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        return jwtService.getUserByPhoneNumber(phoneNumber);
    }

    @PostMapping("/register")
    public void register(@RequestBody RequestRegister requestRegister) {
        jwtService.register(requestRegister);
    }

    @PostMapping("/login")
    public DoubleToken login(@RequestBody RequestLogin requestLogin){
        return jwtService.login(requestLogin);
    }
}
