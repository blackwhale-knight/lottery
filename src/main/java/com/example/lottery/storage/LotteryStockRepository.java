package com.example.lottery.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LotteryStockRepository {

    private static final String STOCK_KEY = "lottery:stock";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Integer getStock(String prizeId) {
        String stock = (String) redisTemplate.opsForHash().get(STOCK_KEY, prizeId);
        return stock != null ? Integer.parseInt(stock) : 0;
    }

    public void setStock(String prizeId, int stock) {
        redisTemplate.opsForHash().put(STOCK_KEY, prizeId, String.valueOf(stock));
    }

}
