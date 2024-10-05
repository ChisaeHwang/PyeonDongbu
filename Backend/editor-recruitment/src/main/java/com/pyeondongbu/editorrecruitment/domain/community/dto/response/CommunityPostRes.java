package com.pyeondongbu.editorrecruitment.domain.community.dto.response;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityPostRes {

    private Long id;
    private String title;
    private String content;
    private String memberName;
    private List<String> tagNames;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;
    private Boolean isAuthor;

    /**
     * 게시글 하나 조회 시
     */

    public static CommunityPostRes from(
            final CommunityPost post,
            final Boolean isAuthor
    ) {
        return CommunityPostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(getTagsNameList(post))
                .memberName(post.getMember().getNickname())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .isAuthor(isAuthor)
                .build();
    }

    /**
     * 전체 게시글 조회 시
     */

    public static CommunityPostRes from(
            final CommunityPost post
    ) {
        return CommunityPostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberName(post.getMember().getNickname())
                .tagNames(getTagsNameList(post))
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .build();
    }

    private static List<String> getTagsNameList(CommunityPost post) {
        return post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

}
