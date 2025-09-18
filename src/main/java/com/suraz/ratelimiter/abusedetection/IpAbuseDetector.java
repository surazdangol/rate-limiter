package com.suraz.ratelimiter.abusedetection;

import com.suraz.ratelimiter.core.RateLimiter;
import com.suraz.ratelimiter.tokenbucket.TokenBucketService;
import com.suraz.ratelimiter.tokenbucket.TokenPolicy;
import com.suraz.ratelimiter.util.RedisKeyGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class IpAbuseDetector implements RateLimiter {

  private final TokenBucketService bucketService;

  private final AbuseDetectionConfig.Config config;

  public IpAbuseDetector(TokenBucketService bucketService, AbuseDetectionConfig config) {
    this.bucketService = bucketService;
    this.config = config.getIp();
  }

  @Override
  public boolean hasLimitExceeded(HttpServletRequest req) {
    return !bucketService.isTokenAvailable(
        RedisKeyGenerator.generate(config.getPrefix(), req.getRemoteAddr()),
        TokenPolicy.from(config));
  }


}
