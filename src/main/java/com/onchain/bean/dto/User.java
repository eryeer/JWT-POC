package com.onchain.bean.dto;

import com.onchain.bean.BaseInfo;
import lombok.Data;

/**
 * @Classname User
 * @Description TODO
 * @Date 2020/7/10 16:47
 * @Created by zhaochen
 */
@Data
public class User extends BaseInfo {

    private String name;

    private String password;

    private String phoneNumber;

    private String authority;
}
