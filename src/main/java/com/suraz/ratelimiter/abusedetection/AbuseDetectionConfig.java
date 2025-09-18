package com.suraz.ratelimiter.abusedetection;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "codeavatar.abuse-detection")
public class AbuseDetectionConfig {

  private Config ip;
  private Config user;

  public Config getIp() { return ip; }

  public Config getUser() { return user; }

  public static class Config {
    private String prefix;
    private String interval;
    private int requestCount;

    public String getPrefix() { return prefix; }

    public String getInterval() { return interval; }

    public int getRequestCount() { return requestCount; }
  }
}