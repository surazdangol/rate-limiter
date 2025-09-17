package com.suraz.ratelimiter.core;

import com.suraz.ratelimiter.tier.Tier;

public interface RateLimiter {

    public boolean hasLimitExceeded(String apiKey, String ipAddress, Tier tier);

}
