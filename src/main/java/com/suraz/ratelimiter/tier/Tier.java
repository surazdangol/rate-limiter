package com.suraz.ratelimiter.tier;

import java.util.Optional;

public enum Tier {
  FREE(100),
  BASIC(1000),
  PRO(null);

  private Integer dailyLimit;

  public Optional<Integer> dailyLimit() {
    return Optional.ofNullable(dailyLimit);
  }

  Tier(Integer dailyLimit) {
    this.dailyLimit = dailyLimit;
  }
}
