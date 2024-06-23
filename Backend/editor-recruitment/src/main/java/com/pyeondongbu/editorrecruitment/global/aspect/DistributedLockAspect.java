package com.pyeondongbu.editorrecruitment.global.aspect;

import com.pyeondongbu.editorrecruitment.global.annotation.DistributedLock;
import com.pyeondongbu.editorrecruitment.global.config.redis.RedisKey;
import com.pyeondongbu.editorrecruitment.global.dto.ExecuteWithLockParam;
import com.pyeondongbu.editorrecruitment.global.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedisLockService redisLockService;

    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.pyeondongbu.editorrecruitment.global.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        final RedisKey key = RedisKey.getLockByMethodName(method.getName());

        final ExecuteWithLockParam param = ExecuteWithLockParam.builder()
                .key(key)
                .waitTime(distributedLock.waitTime())
                .leaseTime(distributedLock.leaseTime())
                .timeUnit(distributedLock.timeUnit())
                .supplier(() -> aopForTransaction.proceed(joinPoint))
                .build();

        return redisLockService.executeWithLock(param);
    }

}

