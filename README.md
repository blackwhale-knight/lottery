# Lottery Application
## 需求
> 設計一個電商轉盤抽獎功能，3種獎品各有 N 種數量且每個獎品的中獎機率不同，與銘謝惠顧合起來機率為100%，可有同時多次抽獎的機會並包含防止重複抽獎與獎品超抽的情況。

## 流程設計
1. 用戶請求抽獎
2. 檢查是否符合抽獎條件（如消費滿額）
3. 從 Redis 或資料庫取得獎品數量
4. 隨機機率計算獲得的獎品
5. 確認獎品庫存：
   1. 若足夠，扣除庫存，記錄中獎資訊 
   2. 若不足，重新抽選或回退到「銘謝惠顧」
6. 回應用戶中獎結果

## Redis
### Start Redis
```shell
redis-server
```

### Check Prize Stock
```shell
redis-cli
HGETALL lottery:prize_stock
```