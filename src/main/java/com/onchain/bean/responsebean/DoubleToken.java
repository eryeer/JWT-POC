package com.onchain.bean.responsebean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname DoubleToken
 * @Description TODO
 * @Date 2020/7/10 17:44
 * @Created by zhaochen
 */
@Data
@NoArgsConstructor
public class DoubleToken {

    private String accessToken;
    private String refreshToken;

    public DoubleToken(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
