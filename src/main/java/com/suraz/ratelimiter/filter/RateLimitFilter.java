package com.suraz.ratelimiter.filter;

import com.suraz.ratelimiter.core.RateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private final List<RateLimiter> rateLimiters;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    HttpServletRequest req = (HttpServletRequest) request;
    String apiKey = req.getHeader("x-api-key");
    if (Objects.isNull(apiKey)) {
      ((HttpServletResponse) response).sendError(401, "Unauthorized");
      return;
    }

    for (RateLimiter limiter : rateLimiters) {
      if (limiter.hasLimitExceeded(req)) {

        ((HttpServletResponse) response).sendError(429, "Rate Limit Exceeded");

        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
