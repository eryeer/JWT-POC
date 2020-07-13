package com.onchain.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by leo on 2017/8/10.
 */
@Data
public class ResponseFormat<T> {
    private Integer returnCode;
    private String returnDesc;
    private List<T> items;
    private T data;

    public ResponseFormat(){

    }

    public ResponseFormat(int code){
        setReturnCode(code);
    }


}
