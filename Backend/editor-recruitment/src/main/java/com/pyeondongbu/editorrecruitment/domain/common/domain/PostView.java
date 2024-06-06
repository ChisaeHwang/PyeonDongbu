package com.pyeondongbu.editorrecruitment.domain.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@RedisHash(value = "postView", timeToLive = 3600) // 1시간 TTL 설정
public class PostView {
    @Id
    private String id;
    private Long postId;
    private String ipAddress;
}
