package com.example.securityapi.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 5;
  private static final long LOCK_TIME_DURATION = 15 * 60 * 1000;

  private final ConcurrentHashMap<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

  public void loginSucceeded(String key) {
    attemptsCache.remove(key);
  }

  public void loginFailed(String key) {
    AttemptInfo attemptInfo = attemptsCache.computeIfAbsent(key, k -> new AttemptInfo());
    attemptInfo.incrementAttempts();
    attemptInfo.setLastAttemptTime(System.currentTimeMillis());
  }

  public boolean isBlocked(String key) {
    AttemptInfo attemptInfo = attemptsCache.get(key);
    if (attemptInfo == null) {
      return false;
    }

    if (attemptInfo.getAttempts() >= MAX_ATTEMPTS) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - attemptInfo.getLastAttemptTime() > LOCK_TIME_DURATION) {
        attemptsCache.remove(key);
        return false;
      }
      return true;
    }

    return false;
  }

  public long getRemainingLockTime(String key) {
    AttemptInfo attemptInfo = attemptsCache.get(key);
    if (attemptInfo == null || attemptInfo.getAttempts() < MAX_ATTEMPTS) {
      return 0;
    }

    long currentTime = System.currentTimeMillis();
    long elapsedTime = currentTime - attemptInfo.getLastAttemptTime();
    return Math.max(0, LOCK_TIME_DURATION - elapsedTime);
  }

  private static class AttemptInfo {
    private final AtomicInteger attempts = new AtomicInteger(0);
    private volatile long lastAttemptTime;

    public int incrementAttempts() {
      return attempts.incrementAndGet();
    }

    public int getAttempts() {
      return attempts.get();
    }

    public void setLastAttemptTime(long lastAttemptTime) {
      this.lastAttemptTime = lastAttemptTime;
    }

    public long getLastAttemptTime() {
      return lastAttemptTime;
    }
  }
}
