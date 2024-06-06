package com.pyeondongbu.editorrecruitment.domain.auth.annotation;



import java.util.Arrays;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.global.exception.AdminException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_ADMIN_AUTHORITY;

@Aspect
@Component
public class AdminOnlyChecker {

    @Before("@annotation(com.pyeondongbu.editorrecruitment.domain.auth.annotation.AdminOnly)")
    public void check(final JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isAdmin)
                .findFirst()
                .orElseThrow(() -> new AdminException(INVALID_ADMIN_AUTHORITY));
    }
}
