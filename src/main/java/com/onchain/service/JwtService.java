package com.onchain.service;

import com.alibaba.fastjson.JSON;
import com.onchain.bean.dto.User;
import com.onchain.bean.requestbean.RequestAuthorize;
import com.onchain.bean.requestbean.RequestLogin;
import com.onchain.bean.requestbean.RequestRefresh;
import com.onchain.bean.requestbean.RequestRegister;
import com.onchain.bean.responsebean.DoubleToken;
import com.onchain.mapper.JwtMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * @Classname JwtService
 * @Description TODO
 * @Date 2020/7/10 16:55
 * @Created by zhaochen
 */
@Service
public class JwtService {
    private static final long ACCESS_EXPIRE_TIME = 5 * 60 * 1000;
    private static final long REFRESH_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
    private static final String TTYPE_ACCESS = "access";
    private static final String TTYPE_REFRESH = "refresh";
    private static final String SECRET_CODE = "12345677";
    private static final String AUTHORITY_A = "auth_a";

    @Autowired
    private JwtMapper jwtMapper;

    @Autowired
    private RedisService redisService;

    public User getUserByPhoneNumber(String phoneNumber) {
        return jwtMapper.getUserByPhoneNumber(phoneNumber);
    }

    public Long register(RequestRegister requestRegister) {
        User user = new User();
        user.setName(requestRegister.getName());
        user.setPassword(requestRegister.getPassword());
        user.setPhoneNumber(requestRegister.getPhoneNumber());
        user.setAuthority("[]");
        jwtMapper.insertUser(user);
        return user.getId();
    }

    public String createToken(String userId, String ttype) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        long nowMillis = System.currentTimeMillis();//生成JWT的时间
        Date now = new Date(nowMillis);
        String jwtId = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<String, Object>();//创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        claims.put("ttyp", ttype);
        SecretKey secretKey = createSecretKey();//生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        //下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder() //这里其实就是new一个JwtBuilder，设置jwt的body
                .setClaims(claims)          //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setId(jwtId)                  //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setIssuedAt(now)      //iat: jwt的签发时间
                .setSubject(userId)        //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .signWith(signatureAlgorithm, secretKey);//设置签名使用的签名算法和签名使用的秘钥
        //设置过期时间
        if (StringUtils.equals(ttype, TTYPE_ACCESS)) {
            long expMillis = nowMillis + ACCESS_EXPIRE_TIME;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
            String authorities = redisService.getValue("authority_" + userId);
            claims.put("dom", authorities);
        }
        if (StringUtils.equals(ttype, TTYPE_REFRESH)) {
            long expMillis = nowMillis + REFRESH_EXPIRE_TIME;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        String newToken = "jwt_" + builder.compact();
        return newToken;
    }

    // 签名私钥
    private SecretKey createSecretKey() {
        byte[] encodedKey = SECRET_CODE.getBytes();
        // byte[] encodedKey = Base64.decodeBase64(signKey);//本地的密码解码
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");// 根据给定的字节数组使用AES加密算法构造一个密钥，使用 encodedKey中的始于且包含 0 到前 leng 个字节这是当然是所有。（后面的文章中马上回推出讲解Java加密和解密的一些算法）
        return secretKey;
    }

    public DoubleToken login(RequestLogin requestLogin) {
        User user = jwtMapper.getUserByPhoneNumber(requestLogin.getPhoneNumber());
        if (!StringUtils.equals(user.getPassword(), requestLogin.getPassword())) {
            throw new RuntimeException();
        }
        String accessToken = createToken(user.getId() + "", TTYPE_ACCESS);
        String refreshToken = createToken(user.getId() + "", TTYPE_REFRESH);
        redisService.setValueEX("user_token_" + user.getId(), refreshToken, REFRESH_EXPIRE_TIME / 1000);
        redisService.setValueEX("authority_" + user.getId(), user.getAuthority(), REFRESH_EXPIRE_TIME / 1000);
        return new DoubleToken(accessToken, refreshToken);
    }


    public String refresh(RequestRefresh requestRefresh) {
        String refreshToken = requestRefresh.getRefreshToken();
        SecretKey secretKey = this.createSecretKey();
        String userId = Jwts.parser()   //得到DefaultJwtParser
                .setSigningKey(secretKey)  //设置签名的秘钥
                .parseClaimsJws(refreshToken.replace("jwt_", ""))
                .getBody().getSubject();
        String refreshTokenServer = redisService.getValue("user_token_" + userId);
        if (!StringUtils.equals(refreshToken, refreshTokenServer)) {
            throw new RuntimeException("refreshToken illegal");
        }
        long expireTime = Jwts.parser()   //得到DefaultJwtParser
                .setSigningKey(secretKey)  //设置签名的秘钥
                .parseClaimsJws(refreshToken.replace("jwt_", ""))
                .getBody().getExpiration().getTime();
        if (expireTime < System.currentTimeMillis()) {
            throw new RuntimeException("refreshToken expired.");
        }
        return createToken(userId, TTYPE_ACCESS);

    }

    public Boolean logout(String userId) {
        redisService.delete("user_token_" + userId);
        redisService.delete("authority_" + userId);
        return true;

    }

    public Boolean authorize(RequestAuthorize requestAuthorize) {
        User user = jwtMapper.getUserByPhoneNumber(requestAuthorize.getPhoneNumber());
        List<String> newAuthorities = JSON.parseArray(requestAuthorize.getAuthority(), String.class);
        jwtMapper.updateAuthorizeByPhoneNumber(requestAuthorize.getPhoneNumber(), JSON.toJSONString(newAuthorities));
        redisService.setValue("authority_" + user.getId(), JSON.toJSONString(newAuthorities));
        return true;
    }

    public Boolean checkAccess(String accessToken) {
        boolean retA = checkToken(accessToken);
        boolean retB = checkAuthority(accessToken);
        return retA & retB;
    }

    public boolean checkToken(String token) {
        SecretKey secretKey = this.createSecretKey();
        try {
            long expireTime = Jwts.parser()   //得到DefaultJwtParser
                    .setSigningKey(secretKey)  //设置签名的秘钥
                    .parseClaimsJws(token.replace("jwt_", ""))
                    .getBody().getExpiration().getTime();
            if (expireTime < System.currentTimeMillis()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checkAuthority(String accessToken) {
        SecretKey secretKey = this.createSecretKey();
        try {
            String authorities = Jwts.parser()   //得到DefaultJwtParser
                    .setSigningKey(secretKey)  //设置签名的秘钥
                    .parseClaimsJws(accessToken.replace("jwt_", ""))
                    .getBody().get("dom", String.class);
            if (JSON.parseArray(authorities, String.class).contains(AUTHORITY_A)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
