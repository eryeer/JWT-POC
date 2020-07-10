package com.onchain.mapper;

import com.onchain.bean.dto.User;
import com.onchain.bean.responsebean.DoubleToken;

/**
 * @Classname JwtMapper
 * @Description TODO
 * @Date 2020/7/10 16:57
 * @Created by zhaochen
 */
public interface JwtMapper {

    User getUserByPhoneNumber(String phoneNumber);

    int insertUser(User user);
}
