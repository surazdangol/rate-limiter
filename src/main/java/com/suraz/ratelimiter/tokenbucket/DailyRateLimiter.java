package com.suraz.ratelimiter.tokenbucket;

import com.suraz.ratelimiter.core.RateLimiter;
import com.suraz.ratelimiter.tier.Tier;
import com.suraz.ratelimiter.tier.TierService;
import com.suraz.ratelimiter.util.RedisKeyGenerator;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DailyRateLimiter implements RateLimiter {

  private final TokenBucketService bucketService;

  private final String prefix;

  private final TierService tierService;

  public DailyRateLimiter(
      TokenBucketService bucketService,
      @Value("${codeavatar.ratelimiter.dailyBucket.prefix}") String prefix,
      TierService tierService) {
    this.bucketService = bucketService;
    this.prefix = prefix;
    this.tierService = tierService;
  }

  @Override
  public boolean hasLimitExceeded(HttpServletRequest req) {
    String apiKey = req.getHeader("x-api-key");
    Tier tier = tierService.getTier(apiKey); // Implement this

    if (tier.dailyLimit().isEmpty()) {
      return false;
    }
    return !bucketService.isTokenAvailable(
        RedisKeyGenerator.generate(prefix, apiKey, req.getRemoteAddr()),
        TokenPolicy.of(tier.dailyLimit().get(), Duration.ofDays(1)));
  }
}
