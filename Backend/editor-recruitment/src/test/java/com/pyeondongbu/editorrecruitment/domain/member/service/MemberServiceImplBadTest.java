package com.pyeondongbu.editorrecruitment.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import com.pyeondongbu.editorrecruitment.global.validation.MemberValidationUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceImplBadTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private MemberDetailsRepository memberDetailsRepository;

    @Mock
    private MemberValidationUtils validationUtils;

    private Validator validator;

    private Member createMember() {
        return new Member(
                1L,
                Role.CLIENT,
                "socialLoginId",
                "testNickname",
                "https://pyeondongbu.s3.ap-northeast-2.amazonaws.com/testImage.jpg"
        );
    }

    private MemberDetails createMemberDetails() {
        return MemberDetails.builder()
                .maxSubs(1000)
                .videoTypes(Arrays.asList("Game", "JustChatting"))
                .editedChannels(Arrays.asList("Channel1", "Channel2"))
                .currentChannels(Arrays.asList("CurrentChannel1"))
                .portfolio("Test Portfolio")
                .skills(Arrays.asList("Skill1", "Skill2"))
                .remarks("Test Remarks")
                .build();
    }

    private MemberDetailsReq createValidMemberDetailsReq() {
        return MemberDetailsReq.of(createMemberDetails());
    }

    private MyPageReq createMyPageReq(
            String nickname,
            String imageUrl,
            Role role,
            MemberDetailsReq memberDetailsReq
    ) {
        Member member = Member.of(
                "socialLoginId",
                nickname,
                imageUrl,
                role
        );

        member.setDetails(MemberDetails.of(member, memberDetailsReq));

        return MyPageReq.of(member);
    }

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        memberService = new MemberServiceImpl(
                memberRepository,
                memberDetailsRepository,
                publisher,
                new MemberValidationUtils(memberRepository, validator)
        );

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(createMember()));
    }

    @DisplayName("존재하지 않는 memberId로 업데이트 시도시 예외 발생")
    @Test
    void updateWithNonExistingMemberId() {
        // given
        final MyPageReq request = createMyPageReq(
                "testNickname",
                "http://example.com/image.jpg",
                Role.CLIENT,
                createValidMemberDetailsReq()
        );

        given(memberRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.updateMyPage(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", NOT_FOUND_MEMBER_ID.getStatus());
    }

    @DisplayName("유효하지 않은 닉네임으로 업데이트 시도시 예외 발생")
    @Test
    void updateWithInvalidNickname() {
        // given
        final MyPageReq request = createMyPageReq(
                "",
                "http://example.com/image.jpg",
                Role.CLIENT,
                createValidMemberDetailsReq()
        );

        // when & then
        assertThatThrownBy(() -> memberService.updateMyPage(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_NICK_NAME.getStatus());
    }

    @DisplayName("유효하지 않은 imageUrl로 업데이트 시도시 예외 발생")
    @Test
    void updateWithInvalidImageUrl() {
        // given
        final MyPageReq request = createMyPageReq(
                "testNickname",
                "",
                Role.CLIENT,
                createValidMemberDetailsReq()
        );

        // when & then
        assertThatThrownBy(() -> memberService.updateMyPage(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_IMAGE_URL.getStatus());
    }

    @DisplayName("유효하지 않은 role로 업데이트 시도시 예외 발생")
    @Test
    void updateWithInvalidRole() {
        // given
        final MyPageReq request = createMyPageReq(
                "testNickname",
                "http://example.com/image.jpg",
                null,
                createValidMemberDetailsReq()
        );

        // when & then
        assertThatThrownBy(() -> memberService.updateMyPage(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_ROLE_INFO.getStatus());
    }

    @DisplayName("유효하지 않은 MemberDetailsReq로 업데이트 시도시 예외 발생")
    @Test
    void updateWithInvalidMemberDetailsReq() {
        // given
        final MemberDetails invalidMemberDetails = MemberDetails.builder()
                .maxSubs(1000)
                .videoTypes(null) // 에러 케이스
                .editedChannels(Arrays.asList("Channel1"))
                .currentChannels(Arrays.asList("CurrentChannel1"))
                .portfolio("Test Portfolio")
                .skills(Arrays.asList("Skill1"))
                .remarks("Test Remarks")
                .build();
        final MemberDetailsReq invalidMemberDetailsReq = MemberDetailsReq.of(invalidMemberDetails);
        final MyPageReq request = createMyPageReq("testNickname", "http://example.com/image.jpg", Role.CLIENT, invalidMemberDetailsReq);

        // when & then
        assertThatThrownBy(() -> memberService.updateMyPage(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_MEMBER_DETAILS.getStatus());
    }


}