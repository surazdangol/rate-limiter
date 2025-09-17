package com.suraz.ratelimiter.filter;

import com.suraz.ratelimiter.core.RateLimiter;
import com.suraz.ratelimiter.tier.Tier;
import com.suraz.ratelimiter.tier.TierService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private final RateLimiter rateLimiter;
  private final TierService tierService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    HttpServletRequest req = (HttpServletRequest) request;
    String apiKey = req.getHeader("x-api-key");
    String ip = req.getRemoteAddr();
    Tier tier = tierService.getTier(apiKey); // Implement this
    if (Objects.isNull(apiKey)) {
      ((HttpServletResponse) response).sendError(401, "Unauthorized");
      return;
    }

    if (!rateLimiter.hasLimitExceeded(apiKey, ip, tier)) {
      ((HttpServletResponse) response).sendError(429, "Rate limit exceeded");

      return;
    }
    filterChain.doFilter(request, response);
  }
}
