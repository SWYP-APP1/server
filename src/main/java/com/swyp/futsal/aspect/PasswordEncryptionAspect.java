package com.swyp.futsal.aspect;

import com.swyp.futsal.annotation.PasswordEncryption;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

@Aspect
@Configuration
@RequiredArgsConstructor
public class PasswordEncryptionAspect {

  private final PasswordEncoder passwordEncoder;

  @Around("execution(* com.swyp.futsal.api..*.*(..))")
  public Object encryptPassword(ProceedingJoinPoint joinPoint) throws Throwable {
    Arrays.stream(joinPoint.getArgs())
        .forEach(this::fieldEncryption);

    return joinPoint.proceed();
  }

  private void fieldEncryption(Object o) {
    if (ObjectUtils.isEmpty(o)) {
      return;
    }

    FieldUtils.getAllFieldsList(o.getClass()).stream()
        .filter(field -> field.isAnnotationPresent(PasswordEncryption.class))
        .filter(field -> !(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())))
        .forEach(field -> {
          try {
            Object encryptionField = FieldUtils.readField(field, o, true);
            if (!(encryptionField instanceof String strValue)) {
              return;
            }

            String encrypted = passwordEncoder.encode(strValue);
            FieldUtils.writeField(field, o, encrypted, true);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

}
