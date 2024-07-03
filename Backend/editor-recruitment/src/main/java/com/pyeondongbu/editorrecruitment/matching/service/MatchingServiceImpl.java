package com.pyeondongbu.editorrecruitment.matching.service;

import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import com.pyeondongbu.editorrecruitment.global.validation.MemberValidationUtils;
import com.pyeondongbu.editorrecruitment.matching.domain.Matcher;
import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import com.pyeondongbu.editorrecruitment.matching.dto.MatchingRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage.FIRST_LOGIN_OR_ENTER_DETAILS_CHECK;
import static com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage.SUCCESS_REQUEST;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {


    private final MemberRepository memberRepository;
    private final RecruitmentPostService recruitmentPostService;

    private final MemberValidationUtils memberValidationUtils;
    private final Matcher matcher;

    @Value("${recruitment.tag.default}")
    private String defaultTag;

    public MatchingRes findMatchingPosts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        if(memberValidationUtils.validateMemberDetails(member)){
            return MatchingRes.from(
                    matcher.match(member, null),
                    FIRST_LOGIN_OR_ENTER_DETAILS_CHECK.getMessage()
            );
        }

        List<RecruitmentPostRes> postResList = recruitmentPostService.searchRecruitmentPostsByTags(List.of(defaultTag));
        List<RecruitmentPost> posts = postResList.stream()
                .map(postRes -> postRes.toEntity(member))
                .collect(Collectors.toList());

        return MatchingRes.from(matcher.match(member, posts), SUCCESS_REQUEST.getMessage());
    }


}
