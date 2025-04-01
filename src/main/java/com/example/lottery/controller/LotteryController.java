package com.example.lottery.controller;

import com.example.lottery.entity.Prize;
import com.example.lottery.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lottery")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    @GetMapping("/draw")
    public ResponseEntity<String> draw(@RequestParam String userId) {
        return ResponseEntity.ok(lotteryService.drawLottery(userId));
    }

    @GetMapping("/stock/{prizeId}")
    public ResponseEntity<Integer> getPrizeStock(@PathVariable String prizeId) {
        int stock = lotteryService.getStock(prizeId);
        return ResponseEntity.ok(stock);
    }

    @PostMapping("/update-prizes")
    public ResponseEntity<String> updatePrizes(@RequestBody List<Prize> prizes) {
        String response = lotteryService.updatePrizes(prizes);
        return ResponseEntity.ok(response);
    }
}