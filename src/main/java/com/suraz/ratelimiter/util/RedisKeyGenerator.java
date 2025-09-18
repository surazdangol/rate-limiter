package com.suraz.ratelimiter.util;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeyGenerator {

  public static String generate(String prefix, String... keyParts) {
    if (StringUtils.isBlank(prefix)) {
      throw new IllegalArgumentException("Invalid key");
    }

    String joined =
        Arrays.stream(Objects.nonNull(keyParts) ? keyParts : new String[0])
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(":"));

    return joined.isEmpty() ? prefix : prefix + ":" + joined;
  }
}
