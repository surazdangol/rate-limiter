package com.suraz.ratelimiter.tokenbucket;

import com.suraz.ratelimiter.core.RateLimiter;
import com.suraz.ratelimiter.tier.Tier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DailyRateLimiter implements RateLimiter {

  private final TokenBucketService bucketService;


  private final String prefix;

  public DailyRateLimiter(TokenBucketService bucketService, @Value("${codeavatar.ratelimiter.dailyBucket.prefix}")String prefix) {
    this.bucketService = bucketService;
    this.prefix = prefix;
  }

  private String key(String apiKey, String ipAddress) {
    return prefix + ":" + apiKey + ":" + ipAddress;
  }

  @Override
  public boolean hasLimitExceeded(String apiKey, String ipAddress, Tier tier) {
    if (tier.dailyLimit().isEmpty()) {
      return false;
    }
    return bucketService.isTokenAvailable(key(apiKey, ipAddress), tier);
  }
}
