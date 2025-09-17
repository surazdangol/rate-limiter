package com.suraz.ratelimiter.tokenbucket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenBucket {
  private int tokenCount;
  private long lastRefillTimeInMillis;



    public void addTokens(int tokenCount) {
     this.tokenCount  = this.tokenCount + tokenCount;
     this.lastRefillTimeInMillis = System.currentTimeMillis();
  }

  public void removeToken() {
     this.tokenCount -=1;
  }


}
