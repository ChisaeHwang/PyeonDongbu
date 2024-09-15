package com.pyeondongbu.editorrecruitment.domain.member.service;

import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.MemberSpecification;
import com.pyeondongbu.editorrecruitment.domain.image.domain.S3ImageEvent;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MemberRes;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.validation.MemberValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final String IMAGE_URL_HOST = "pyeondongbu.s3.ap-northeast-2.amazonaws.com";

    private final MemberRepository memberRepository;
    private final MemberDetailsRepository memberDetailsRepository;
    private final ApplicationEventPublisher publisher;
    private final MemberValidationUtils validationUtils;

    @Override
    @Transactional(readOnly = true)
        public MemberRes getMember(final Long memberId) {
        Member member = memberRepository.findByIdWithDetails(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));

        return new MemberRes(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MyPageRes getMyPage(final Long memberId) {
        final Member member = memberRepository.findByIdWithDetails(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MyPageRes.from(member);
    }

    @Override
    @Transactional
    public MyPageRes updateMyPage(final Long memberId, final MyPageReq myPageReq) {
        final Member member = memberRepository.findByIdWithDetails(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        validationUtils.validateMyPageReq(myPageReq, member);
        deletePrevImage(member.getImageUrl(), myPageReq.getImageUrl());
        final Member updatedMember = updateMember(myPageReq, member);

        return MyPageRes.from(updatedMember);
    }


    @Override
    @Transactional(readOnly = true)
    public List<MyPageRes> searchMembers(
            final Integer maxSubs,
            final String role,
            final List<String> skills,
            final List<String> videoTypes
    ) {
        Role roleEnum = Role.isValidRole(role) ? Role.valueOf(role) : null;
        Specification<Member> spec = MemberSpecification.combineSpecifications(
                maxSubs,
                roleEnum,
                skills,
                videoTypes
        );

        return memberRepository.findAll(spec).stream()
                .map(MyPageRes::from)
                .collect(Collectors.toList());
    }

    /**
     * Private 함수들
     */

    private void deletePrevImage(final String prevUrl, final String nextUrl) {
        if (prevUrl.equals(nextUrl)) {
            return;
        }
        try {
            final URL targetUrl = new URL(prevUrl);
            if (targetUrl.getHost().equals(IMAGE_URL_HOST)) {
                final String targetName = prevUrl.substring(prevUrl.lastIndexOf("/") + 1);
                publisher.publishEvent(new S3ImageEvent(targetName));
            }
        } catch (final MalformedURLException e) {
            throw new BadRequestException(INVALID_IMAGE_URL);
        }
    }

    private Member updateMember(
            final MyPageReq myPageReq,
            final Member member
    ) {
        member.update(myPageReq);
        MemberDetails memberDetails = member.getDetails();
        if (memberDetails == null) {
            memberDetails = MemberDetails.of(member, myPageReq.getMemberDetails());
            member.setDetails(memberDetails);
        } else {
            memberDetails.update(myPageReq.getMemberDetails());
        }
        memberDetailsRepository.save(memberDetails);
        return memberRepository.save(member);
    }
}
