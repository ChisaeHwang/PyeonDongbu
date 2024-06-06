package com.pyeondongbu.editorrecruitment.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;

import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(MockitoExtension.class)
@Transactional
class MemberServiceImplTest {


    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private MemberDetailsRepository memberDetailsRepository;

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
                .videoTypes(Arrays.asList("Gaming"))
                .editedChannels(Arrays.asList("Channel1", "Channel2"))
                .currentChannels(Arrays.asList("CurrentChannel1"))
                .portfolio("Test Portfolio")
                .skills(Arrays.asList("Skill1", "Skill2"))
                .remarks("Test Remarks")
                .build();
    }
    @DisplayName("멤버의 닉네임과 프로필 사진을 조회 가능")
    @Test
    void getMyPage() {
        // given
        final Member member = createMember();
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));

        // when
        final MyPageRes actual = memberService.getMyPage(member.getId());

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(MyPageRes.from(member));

    }

    @DisplayName("닉네임 변경 및 프로필 사진 변경 가능")
    @Test
    void updateMyPage() {
        // given
        final MemberDetails memberDetails = createMemberDetails();
        final MyPageReq request = MyPageReq.of(
               createMember()
        );
        final Member member = createMember();
        final Member expectedMember = Member.builder()
                .socialLoginId("testSocialLoginId")
                .nickname(request.getNickname())
                .imageUrl(request.getImageUrl())
                .build();

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(member));
        given(memberRepository.save(any()))
                .willReturn(expectedMember);

        // when
        memberService.updateMyPage(member.getId(), request);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());

    }

}