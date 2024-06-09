package com.pyeondongbu.editorrecruitment.domain.member.service;

import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.MemberSpecification;
import com.pyeondongbu.editorrecruitment.domain.image.domain.S3ImageEvent;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MemberDetailsRes;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.validation.MemberValidationUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
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
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));
    }

    @Override
    @Transactional
    public MyPageRes getMyPage(Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MyPageRes.from(member);
    }

    @Override
    public MyPageRes updateMyPage(Long memberId, MyPageReq myPageReq) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        validationUtils.validateMyPageReq(myPageReq, member);

        final Member updateMember = new Member(
                memberId,
                myPageReq.getRole(),
                member.getSocialLoginId(),
                myPageReq.getNickname(),
                myPageReq.getImageUrl()
        );
        MemberDetails memberDetails = MemberDetails.of(
                member,
                myPageReq.getMemberDetails()
        );
        updateMember.setDetails(memberDetails);
        deletePrevImage(member.getImageUrl(), updateMember.getImageUrl());
        memberDetailsRepository.save(memberDetails);
        memberRepository.save(updateMember);

        return MyPageRes.from(updateMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyPageRes> searchMembers(
            Integer maxSubs,
            String role,
            List<String> skills,
            List<String> videoTypes
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
}
