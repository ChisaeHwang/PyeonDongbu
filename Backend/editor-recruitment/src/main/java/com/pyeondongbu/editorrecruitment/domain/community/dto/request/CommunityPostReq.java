package com.pyeondongbu.editorrecruitment.domain.community.dto.request;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityPostReq {

    @NotBlank(message = "제목은 공백이 될 수 없습니다.")
    @Size(max = 20, message = "제목은 20자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 공백이 될 수 없습니다.")
    private String content;

    @Size(min = 1, max = 4, message = "태그는 1개 이상 4개 이하로 선택해야 합니다.")
    private List<String> tagNames;

    public static CommunityPostReq of(final CommunityPost communityPost) {
        return CommunityPostReq.builder()
                .title(communityPost.getTitle())
                .content(communityPost.getContent())
                .build();
    }

}
