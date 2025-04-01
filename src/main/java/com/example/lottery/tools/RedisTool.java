package com.example.lottery.tools;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RedisTool {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void initStock() {
        Map<String, String> stock = new HashMap<>();
        stock.put("A", "100");
        stock.put("B", "50");
        stock.put("C", "30");
        redisTemplate.opsForHash().putAll("lottery:prize_stock", stock);
    }
}
