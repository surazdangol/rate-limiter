package com.suraz.ratelimiter.tier;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class TierService {
  // TODO: 16/09/2025 fetch tier dynamically based on apiKey
  public Tier getTier(String apiKey) {
    return Tier.FREE;
  }
}
