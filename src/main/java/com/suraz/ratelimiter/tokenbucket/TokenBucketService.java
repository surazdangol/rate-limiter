package com.suraz.ratelimiter.tokenbucket;

import java.time.Duration;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBucketService {

  private final RedisTemplate<String, TokenBucket> redisTemplate;

  public TokenBucketService(RedisTemplate<String, TokenBucket> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public boolean isTokenAvailable(String key, TokenPolicy policy) {
    TokenBucket bucket = redisTemplate.opsForValue().get(key);
    if (Objects.isNull(bucket)) {
      bucket = new TokenBucket(policy.limit(), System.currentTimeMillis());
      redisTemplate.opsForValue().set(key, bucket, policy.ttl());
    }

    if (isRefillRequired(bucket, policy.ttl())) {
      refill(bucket, policy.limit());
    }

    boolean hasToken = bucket.getTokenCount() > 0;
    if (hasToken) {
      bucket.removeToken();
      redisTemplate.opsForValue().set(key, bucket);
    }
    return hasToken;
  }

  private boolean isRefillRequired(TokenBucket bucket, Duration expiryDuration) {
    if (Objects.isNull(bucket)) {
      return true;
    }

    return (System.currentTimeMillis() - bucket.getLastRefillTimeInMillis())
        > expiryDuration.toMillis();
  }

  private void refill(TokenBucket bucket, Integer limit) {

    bucket.addTokens(limit - bucket.getTokenCount());
  }
}
