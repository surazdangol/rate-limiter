package com.suraz.ratelimiter.core;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimiter {

    public boolean hasLimitExceeded(HttpServletRequest req);

}
