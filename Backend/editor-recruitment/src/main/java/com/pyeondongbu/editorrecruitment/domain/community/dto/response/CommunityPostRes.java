package com.pyeondongbu.editorrecruitment.domain.community.dto.response;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityPostRes {

    private Long id;
    private String title;
    private String content;
    private String memberName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;

    public static CommunityPostRes from(
            final CommunityPost post
    ) {
        return CommunityPostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberName(post.getMember().getNickname())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .build();
    }

}
