package com.onchain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhaochen on 3/22/18
 */
@Service
public class RedisService {
    @Autowired(required = false)
    private RedisTemplate<Object, Object> redisTemplate;
    private ValueOperations<Object, Object> valueOperations;
    private ListOperations<Object, Object> listOperations;
    private HashOperations hashOperations;

    @PostConstruct
    public void getValueOperation() {
        if (redisTemplate != null) {
            StringRedisSerializer serializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(serializer);
            redisTemplate.setValueSerializer(serializer);
            redisTemplate.setHashKeySerializer(serializer);
            redisTemplate.setHashValueSerializer(serializer);
            valueOperations = redisTemplate.opsForValue();
            listOperations = redisTemplate.opsForList();
            hashOperations = redisTemplate.opsForHash();
        }
    }

    public void setValue(String key, String value) {
        valueOperations.set(key, value);
    }

    public String getValue(String key) {
        return (String) valueOperations.get(key);
    }

    public void setValueEX(String key, String value, Long second) {
        valueOperations.set(key, value, second, TimeUnit.SECONDS);
    }

    public Boolean setValueIfAbsent(String key, String value) {
        return valueOperations.setIfAbsent(key, value);
    }

    public Boolean setValueIfAbsent(String key, String value, Long second) {
        Boolean result = valueOperations.setIfAbsent(key, value);
        redisTemplate.expire(key, second, TimeUnit.SECONDS);
        return result;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public Boolean expire(String key, Long second) {
        return redisTemplate.expire(key, second, TimeUnit.SECONDS);
    }

    public void addList(String key, String value) {
        listOperations.leftPush(key, value);
    }

    public Long getListSize(String key) {
        return listOperations.size(key);
    }

    public void setHashMap(String key, Map map) {
        hashOperations.putAll(key, map);
    }

    public Map getHashMap(String key) {
        return hashOperations.entries(key);
    }

    public Long deleteMapFields(String key, String... fields) {
        return hashOperations.delete(key, fields);
    }

}
