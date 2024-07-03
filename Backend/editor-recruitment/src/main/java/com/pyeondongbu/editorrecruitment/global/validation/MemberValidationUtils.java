package com.pyeondongbu.editorrecruitment.global.validation;

import com.pyeondongbu.editorrecruitment.domain.common.dto.request.DetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class MemberValidationUtils {

    private final MemberRepository memberRepository;
    private final Validator validator;

    public void validateMyPageReq(final MyPageReq myPageReq, final Member existingMember) {
        if (myPageReq.getRole() == null || !Role.isValidRole(myPageReq.getRole().name())) {
            throw new BadRequestException(INVALID_ROLE_INFO);
        }
        if (myPageReq.getImageUrl() == null || myPageReq.getImageUrl().trim().isEmpty()) {
            throw new BadRequestException(INVALID_IMAGE_URL);
        }
        if (myPageReq.getNickname() == null || myPageReq.getNickname().trim().isEmpty()) {
            throw new BadRequestException(INVALID_NICK_NAME);
        }
        try {
            new URL(myPageReq.getImageUrl());
        } catch (MalformedURLException e) {
            throw new BadRequestException(INVALID_IMAGE_URL);
        }
        if (existingMember.nickNameCheck(myPageReq.getNickname())) {
            duplicatedNickNameCheck(myPageReq.getNickname());
        }

        validateMemberDetailsReq(myPageReq.getMemberDetails());
    }

    public void duplicatedNickNameCheck(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BadRequestException(DUPLICATED_MEMBER_NICKNAME);
        }
    }

    public void validateMemberDetailsReq(final MemberDetailsReq memberDetailsReq) {
        if (memberDetailsReq == null) {
            throw new BadRequestException(INVALID_MEMBER_DETAILS);
        }
        Set<ConstraintViolation<MemberDetailsReq>> violations = validator.validate(memberDetailsReq);
        if (!violations.isEmpty()) {
            throw new BadRequestException(INVALID_MEMBER_DETAILS);
        }
    }

    public boolean validateMemberDetails(final Member member) {
        if (member.getRole() == Role.GUEST) {
            return false;
        }

        if (member.getDetails() == null) {
            return false;
        }

        Set<ConstraintViolation<MemberDetails>> violations = validator.validate(member.getDetails());
        if (!violations.isEmpty()) {
            return false;
        }

        return true;
    }
}
