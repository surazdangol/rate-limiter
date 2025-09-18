package com.suraz.ratelimiter.tokenbucket;

import com.suraz.ratelimiter.abusedetection.AbuseDetectionConfig;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public record TokenPolicy(int limit, Duration ttl) {
  public static TokenPolicy of(int limit, Duration ttl) {
    return new TokenPolicy(limit, ttl);
  }

  public static TokenPolicy from(AbuseDetectionConfig.Config config) {
    TimeUnit timeUnit;
    try {
      timeUnit = TimeUnit.valueOf(config.getInterval().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid interval in config: " + config.getInterval(), e);
    }

    Duration duration =
        switch (timeUnit) {
          case NANOSECONDS -> Duration.ofNanos(1);
          case MICROSECONDS -> Duration.ofNanos(1_000);
          case MILLISECONDS -> Duration.ofMillis(1);
          case SECONDS -> Duration.ofSeconds(1);
          case MINUTES -> Duration.ofMinutes(1);
          case HOURS -> Duration.ofHours(1);
          case DAYS -> Duration.ofDays(1);
        };

    return new TokenPolicy(config.getRequestCount(), duration);
  }
}
