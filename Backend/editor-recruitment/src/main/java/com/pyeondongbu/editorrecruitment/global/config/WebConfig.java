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

    @Value("${skills.list}")
    private List<String> allSkills;

    @Value("${video-types.list}")
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
        return new Vectorizer(allSkills, allVideoTypes);
    }

}
