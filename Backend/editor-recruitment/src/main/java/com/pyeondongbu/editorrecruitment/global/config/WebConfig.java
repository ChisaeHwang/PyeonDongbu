package com.pyeondongbu.editorrecruitment.global.config;

import com.pyeondongbu.editorrecruitment.domain.auth.configuration.AccessorResolver;
import com.pyeondongbu.editorrecruitment.domain.auth.configuration.AuthenticationInterceptor;
import com.pyeondongbu.editorrecruitment.domain.auth.configuration.UserAuthorityInterceptor;
import com.pyeondongbu.editorrecruitment.matching.domain.Vectorizer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final UserAuthorityInterceptor userAuthorityInterceptor;
    private final AccessorResolver accessorResolver;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Value("${allSkills}")
    private List<String> allSkills;

    @Value("${allVideoTypes}")
    private List<String> allVideoTypes;

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessorResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry
                .addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .order(0);

        registry
                .addInterceptor(userAuthorityInterceptor)
                .addPathPatterns("/**")
                .order(1);

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public Validator getValidator() {
        return validator();
    }


    @Bean
    public Vectorizer vectorizer() {
        List<String> allSkills = Arrays.asList("Java", "Spring Boot", "React", "MySQL", "Python", "Django", "JavaScript", "C++", "SQL", "Kotlin", "Android", "Swift", "iOS", "Ruby", "Rails", "PHP", "Laravel", "Node.js", "MongoDB", "AWS", "DevOps");
        List<String> allVideoTypes = Arrays.asList("튜토리얼", "라이브 코딩", "코드 리뷰", "강의", "세미나", "워크샵", "코딩 챌린지", "기술 토크", "앱 개발", "웹 개발", "기술 데모", "DevOps");

        return new Vectorizer(allSkills, allVideoTypes);
    }

}
