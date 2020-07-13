package com.onchain.mapper;

import com.onchain.bean.dto.User;
import org.apache.ibatis.annotations.*;

/**
 * @Classname JwtMapper
 * @Description TODO
 * @Date 2020/7/10 16:57
 * @Created by zhaochen
 */
public interface JwtMapper {

    @Select("select * from tbl_user where phone_number =#{phoneNumber}")
    User getUserByPhoneNumber(String phoneNumber);

    @Insert("insert into tbl_user (name, password, phone_number, authority) value (#{name},#{password},#{phoneNumber},#{authority})")
    @Options(useGeneratedKeys = true)
    int insertUser(User user);

    @Update("update tbl_user set authority = #{authority} where phone_number = #{phoneNumber}")
    void updateAuthorizeByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("authority") String authority);

    @Select("select * from tbl_user where id =#{id}")
    User getUserById(String id);
}
