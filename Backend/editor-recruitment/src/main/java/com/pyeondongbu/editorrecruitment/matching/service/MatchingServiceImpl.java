package com.pyeondongbu.editorrecruitment.matching.service;

import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import com.pyeondongbu.editorrecruitment.matching.domain.Matcher;
import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {


    private final MemberRepository memberRepository;
    private final RecruitmentPostService recruitmentPostService;
    private final Matcher matcher;

    @Value("${recruitment.tag.default}")
    private String defaultTag;

    public List<MatchingResult> findMatchingPosts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        List<RecruitmentPostRes> postResList = recruitmentPostService.searchRecruitmentPostsByTags(List.of(defaultTag));
        List<RecruitmentPost> posts = postResList.stream()
                .map(postRes -> postRes.toEntity(member))
                .collect(Collectors.toList());

        return matcher.match(member, posts);
    }

    public List<MatchingResult> findTopKMatchingPosts(Long memberId, int k) {
        return findMatchingPosts(memberId).stream()
                .limit(k) // 이거 그냥 뺴도됨 -> 매칭 되면 일단 싹다 가져오는 방향
                .collect(Collectors.toList());
    }

}
