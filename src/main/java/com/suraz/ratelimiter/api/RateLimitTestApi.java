package com.suraz.ratelimiter.api;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("rate-limit-test")
public class RateLimitTestApi {

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Rate Limit passed");
    }
}
