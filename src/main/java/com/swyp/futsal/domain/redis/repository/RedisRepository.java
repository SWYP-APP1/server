package com.swyp.futsal.domain.redis.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  public void setValue(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public Object getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void removeKey(String key) {
    redisTemplate.delete(key);
  }

  public void setValueTtl(String key, String value, Duration ttl) {
    redisTemplate.opsForValue().set(key, value, ttl);
  }
}
