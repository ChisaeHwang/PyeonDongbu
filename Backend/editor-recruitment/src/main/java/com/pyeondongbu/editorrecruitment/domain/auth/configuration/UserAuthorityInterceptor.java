package com.pyeondongbu.editorrecruitment.domain.auth.configuration;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.FAIL_TO_VALIDATE_TOKEN;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Slf4j
@Component
public class UserAuthorityInterceptor implements HandlerInterceptor {

    private static final String MEMBER_KEY = "memberId";

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        MemberOnly member = handlerMethod.getMethodAnnotation(MemberOnly.class);
        if (!Objects.isNull(member)) {
            validateUserAuthorization(request);
        }

        return true;
    }

    private void validateUserAuthorization(final HttpServletRequest request) {
        Accessor accessor = (Accessor)request.getAttribute(MEMBER_KEY);

        if (Objects.isNull(accessor)) {
            throw new MemberException(FAIL_TO_VALIDATE_TOKEN);
        }
    }
}
