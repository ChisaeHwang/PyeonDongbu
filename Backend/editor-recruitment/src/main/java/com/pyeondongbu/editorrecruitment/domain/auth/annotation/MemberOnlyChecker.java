package com.pyeondongbu.editorrecruitment.domain.auth.annotation;


import java.util.Arrays;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_AUTHORITY;

@Aspect
@Component
@Slf4j
public class MemberOnlyChecker {

    @Before("@annotation(com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly)")
    public void check(final JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isMember)
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_AUTHORITY));
    }
}