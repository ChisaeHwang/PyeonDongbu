package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitmentPostTagReq {

    private List<String> tagNames;

    public static RecruitmentPostTagReq of(
            final List<String> tagNames
    ) {
        return new RecruitmentPostTagReq(tagNames);
    }


}
