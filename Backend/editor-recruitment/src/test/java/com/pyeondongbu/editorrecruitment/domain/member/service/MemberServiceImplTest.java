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
import com.pyeondongbu.editorrecruitment.global.validation.MemberValidationUtils;
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

    @Mock
    private MemberValidationUtils memberValidationUtils;

    private Member createMember() {
        Member member = new Member(
                1L,
                Role.CLIENT,
                "socialLoginId",
                "testNickname",
                "https://pyeondongbu.s3.ap-northeast-2.amazonaws.com/testImage.jpg"
        );
        member.setDetails(createMemberDetails());
        return member;
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

    @DisplayName("멤버의 세부 정보를 업데이트 가능")
    @Test
    void updateMemberDetails() {
        // given
        final Member member = createMember();

        final MemberDetailsReq newDetailsReq = MemberDetailsReq.builder()
                .maxSubs(2000)
                .remarks("Updated Remarks")
                .skills(Arrays.asList("UpdatedSkill1", "UpdatedSkill2"))
                .videoTypes(Arrays.asList("UpdatedVideoType1"))
                .editedChannels(Arrays.asList("UpdatedChannel1"))
                .currentChannels(Arrays.asList("UpdatedCurrentChannel1"))
                .portfolio("Updated Portfolio")
                .build();

        final MyPageReq request = MyPageReq.builder()
                .nickname("newNickname")
                .imageUrl("https://newurl.com/newImage.jpg")
                .role(Role.CLIENT)
                .memberDetails(newDetailsReq)
                .build();

        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));
        given(memberDetailsRepository.save(any(MemberDetails.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        memberService.updateMyPage(member.getId(), request);

        // then
        assertThat(member.getNickname()).isEqualTo("newNickname");
        assertThat(member.getImageUrl()).isEqualTo("https://newurl.com/newImage.jpg");
        MemberDetails updatedDetails = member.getDetails();
        assertThat(updatedDetails.getMaxSubs()).isEqualTo(2000);
        assertThat(updatedDetails.getRemarks()).isEqualTo("Updated Remarks");
        assertThat(updatedDetails.getSkills()).containsExactly("UpdatedSkill1", "UpdatedSkill2");
        assertThat(updatedDetails.getVideoTypes()).containsExactly("UpdatedVideoType1");
        assertThat(updatedDetails.getEditedChannels()).containsExactly("UpdatedChannel1");
        assertThat(updatedDetails.getCurrentChannels()).containsExactly("UpdatedCurrentChannel1");
        assertThat(updatedDetails.getPortfolio()).isEqualTo("Updated Portfolio");

        verify(memberRepository).findById(member.getId());
        verify(memberDetailsRepository).save(any(MemberDetails.class));
        verify(memberRepository).save(any(Member.class));
    }



}