package com.org.synchronyEmployeeManagement.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private final RedisTemplate<String, Object> redisTemplate;


    public void cacheEmployeeById(String id, Object employee) {
        redisTemplate.opsForValue().set("employee:" + id, employee);
    }

    public void evictEmployeeCache(Long id) {
        redisTemplate.delete("employee:" + id);
    }

    public Object getEmployeeFromCache(Long id) {
        LOGGER.info("LOGGED-{}", redisTemplate.opsForValue().get("employee:" + id));
        return redisTemplate.opsForValue().get("employee:" + id);
    }

}
