package com.suraz.ratelimiter.tokenbucket;

import static org.junit.jupiter.api.Assertions.*;

import com.suraz.ratelimiter.tier.Tier;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TokenBucketServiceTest {

  private TokenBucketService cut;

  @Mock private RedisTemplate<String, TokenBucket> redisTemplate;

  @Mock private ValueOperations<String, TokenBucket> valueOperations;

  @BeforeEach
  public void init() {

    this.cut = new TokenBucketService(redisTemplate);
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Nested
  class HasToken {
    Tier tier;

    String key;

    @BeforeEach
    public void init() {
      key = "daily:bucket:1234:10.13.194.13";
      tier = Tier.BASIC;
    }

    @Nested
    class WhenApiIsAccessedForTheFirstTime {

      @BeforeEach
      public void init() {

        Mockito.when(valueOperations.get(Mockito.anyString())).thenReturn(null);
      }

      @Test
      public void testReturnsTrue() {
        assertTrue(cut.isTokenAvailable(key, tier));
      }
    }

    @Nested
    class WhenApiIsAccessedMultipleTimes {

      @BeforeEach
      public void init() {}

      @Nested
      class WhenThereIsOnlyOneTokenLeft {
        @BeforeEach
        public void init() {
          Mockito.when(valueOperations.get(Mockito.anyString()))
              .thenReturn(new TokenBucket(1, System.currentTimeMillis()));
        }

        @Nested
        class WhenApiIsAccessOnce {

          @Test
          public void testReturnsTrue() {
            Assertions.assertTrue(cut.isTokenAvailable(key, tier));
          }
        }

        @Nested
        class WhenApiIsAccessedMoreThanOnceSequentially {

          @Test
          public void testReturnsFalse() {
            cut.isTokenAvailable(key, tier);
            Assertions.assertFalse(cut.isTokenAvailable(key, tier));
          }
        }
      }
    }

    @Nested
    class WhenTokenIsNotLeft {

      @Nested
      class WhenTokenRefillPeriodHasNotElapsed {
        @BeforeEach
        public void init() {
          Mockito.when(valueOperations.get(Mockito.anyString()))
              .thenReturn(new TokenBucket(0, System.currentTimeMillis()));
        }

        @Test
        public void testReturnsFalse() {
          Assertions.assertFalse(cut.isTokenAvailable(key, tier));
        }
      }

      @Nested
      class WhenTokenRefillTimeHasElapsed {
        @BeforeEach
        public void init() {
          Mockito.when(valueOperations.get(Mockito.anyString()))
              .thenReturn(
                  new TokenBucket(0, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        }

        @Test
        public void testReturnsTrue() {
          Assertions.assertTrue(cut.isTokenAvailable(key, tier));
        }
      }
    }
  }
}
