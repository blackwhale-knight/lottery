package com.example.lottery.service;

import com.example.lottery.entity.Prize;
import com.example.lottery.storage.LotteryStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class LotteryService {

    private final String PRIZE_STOCK_KEY = "lottery:prize_stock";
    private final String PRIZE_PROBABILITY_KEY = "lottery:prize_probability";
    private final String USER_COOLDOWN_KEY = "lottery:user_cooldown";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LotteryStockRepository stockRepository;

    private static final Random random = new Random();

    public String drawLottery(String userId) {
        // Prevent repeated draws within cooldown period
        if (Boolean.TRUE.equals(redisTemplate.hasKey(USER_COOLDOWN_KEY + userId))) {
            return "Please wait before drawing again.";
        }

        // Fetch prizes and probabilities from Redis
        Map<Object, Object> stockMap = redisTemplate.opsForHash().entries(PRIZE_STOCK_KEY);
        Map<Object, Object> probabilityMap = redisTemplate.opsForHash().entries(PRIZE_PROBABILITY_KEY);

        if (stockMap.isEmpty() || probabilityMap.isEmpty()) {
            return "No prizes available.";
        }

        List<Prize> prizePool = new ArrayList<>();
        for (Object key : probabilityMap.keySet()) {
            String prizeId = (String) key;
            double probability = Double.parseDouble((String) probabilityMap.get(prizeId));
            int stock = Integer.parseInt((String) stockMap.getOrDefault(prizeId, "-1")); // -1 means unlimited

            prizePool.add(new Prize(prizeId, "Prize " + prizeId, probability, stock));
        }

        // Generate a random number for prize selection
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (Prize prize : prizePool) {
            cumulativeProbability += prize.getProbability();
            if (rand <= cumulativeProbability) {
                // Check prize stock and decrement if available
                if (prize.getStock() != -1) { // If not unlimited
                    Long stock = redisTemplate.opsForHash().increment(PRIZE_STOCK_KEY, prize.getId(), -1);
                    if (stock < 0) {
                        redisTemplate.opsForHash().increment(PRIZE_STOCK_KEY, prize.getId(), 1); // Restore stock
                        return "Sorry, the prize is out of stock.";
                    }
                }

                // Set cooldown for the user (10 seconds)
                redisTemplate.opsForValue().set(USER_COOLDOWN_KEY + userId, "1", 10, TimeUnit.SECONDS);

                return "Congratulations! You won: " + prize.getName();
            }
        }
        return "Thank you for participating!";
    }

    public String updatePrizes(List<Prize> prizes) {
        Map<String, String> stockMap = new HashMap<>();
        Map<String, String> probabilityMap = new HashMap<>();

        double totalProbability = 0.0;
        for (Prize prize : prizes) {
            stockMap.put(prize.getId(), String.valueOf(prize.getStock()));
            probabilityMap.put(prize.getId(), String.valueOf(prize.getProbability()));
            totalProbability += prize.getProbability();
        }

        if (Math.abs(totalProbability - 1.0) > 0.001) {
            return "Error: Probabilities must sum up to 1.0.";
        }

        redisTemplate.opsForHash().putAll(PRIZE_STOCK_KEY, stockMap);
        redisTemplate.opsForHash().putAll(PRIZE_PROBABILITY_KEY, probabilityMap);

        return "Prizes updated successfully.";
    }

    public int getStock(String prizeId) {
        return stockRepository.getStock(prizeId);
    }
}