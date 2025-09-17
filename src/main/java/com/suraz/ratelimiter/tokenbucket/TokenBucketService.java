package com.suraz.ratelimiter.tokenbucket;

import com.suraz.ratelimiter.tier.Tier;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBucketService {

  private final RedisTemplate<String, TokenBucket> redisTemplate;


  public TokenBucketService(
      RedisTemplate<String, TokenBucket> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }



  public boolean isTokenAvailable(String key, Tier tier) {
    TokenBucket bucket = redisTemplate.opsForValue().get(key);
    if (Objects.isNull(bucket)) {
      bucket = new TokenBucket(tier.dailyLimit().get(), System.currentTimeMillis());
    }

    if (isRefillRequired(bucket)) {
      refill(bucket, tier);
    }

    boolean hasToken = bucket.getTokenCount() > 0;
    if (hasToken) {
      bucket.removeToken();
      redisTemplate.opsForValue().set(key, bucket);
    }
    return  hasToken;
  }

  private boolean isRefillRequired(TokenBucket bucket) {
    if (Objects.isNull(bucket)) {
      return true;
    }

    return (System.currentTimeMillis() - bucket.getLastRefillTimeInMillis())
        > TimeUnit.DAYS.toMillis(1);
  }

  private void refill(TokenBucket bucket, Tier tier) {

    tier.dailyLimit()
        .ifPresent(
            (max) -> {
              bucket.addTokens(max - bucket.getTokenCount());
            });
  }
}
