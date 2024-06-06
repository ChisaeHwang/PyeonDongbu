package com.pyeondongbu.editorrecruitment.domain.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import org.springframework.data.annotation.PersistenceConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 604800)
public class RefreshToken {
    @Id
    private String token;
    private Long memberId;
}

