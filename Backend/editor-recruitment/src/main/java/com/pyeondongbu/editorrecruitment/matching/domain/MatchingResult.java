package com.pyeondongbu.editorrecruitment.matching.domain;

import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingResult {
    private final MyPageRes myPageRes;
    private final RecruitmentPostRes recruitmentPostRes;
    private final double similarity;

}
