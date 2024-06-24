package com.pyeondongbu.editorrecruitment.domain.member.service;

import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceConcurrencyTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberDetailsRepository memberDetailsRepository;

    private Member member;

    private Member createMember() {
        Member member = new Member(
                null,
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

    private MemberDetailsReq createMemberDetailsReq(int i) {
        return MemberDetailsReq.builder()
                .maxSubs(2000 + i)
                .remarks("Updated Remarks " + i)
                .skills(Arrays.asList("UpdatedSkill1", "UpdatedSkill2"))
                .videoTypes(Arrays.asList("UpdatedVideoType1"))
                .editedChannels(Arrays.asList("UpdatedChannel1"))
                .currentChannels(Arrays.asList("UpdatedCurrentChannel1"))
                .portfolio("Updated Portfolio " + i)
                .build();
    }

    @BeforeEach
    public void setUp() {
        member = createMember();
        member = memberRepository.save(member);
        member = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(member.getVersion()).isNotNull();
    }

    @DisplayName("동시성 테스트 : 멤버 업데이트")
    @Test
    void updateMemberConcurrently() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    MyPageReq req = MyPageReq.builder()
                            .nickname("newNickname" + finalI)
                            .imageUrl("https://newurl.com/newImage" + finalI + ".jpg")
                            .role(Role.CLIENT)
                            .memberDetails(createMemberDetailsReq(finalI))
                            .build();
                    memberService.updateMyPage(member.getId(), req);
                } catch (OptimisticLockingFailureException e) {
                    System.out.println("OptimisticLockingFailureException 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Member updatedMember = memberService.getMember(member.getId());
        assertThat(updatedMember).isNotNull();
    }

    @DisplayName("동시성 테스트  멤버 업데이트 시 충돌 발생 및 재시도")
    @Test
    void updateMemberConcurrentlyWithRetry() throws InterruptedException {
        int numberOfThreads = 20; // 스레드 수를 더 늘려서 테스트
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    boolean success = false;
                    for (int attempt = 0; attempt < 3; attempt++) { // 최대 3번 재시도
                        try {
                            MyPageReq req = MyPageReq.builder()
                                    .nickname("newNickname" + finalI)
                                    .imageUrl("https://newurl.com/newImage" + finalI + ".jpg")
                                    .role(Role.CLIENT)
                                    .memberDetails(createMemberDetailsReq(finalI))
                                    .build();
                            memberService.updateMyPage(member.getId(), req);
                            success = true;
                            break;
                        } catch (OptimisticLockingFailureException e) {
                            System.out.println("OptimisticLockingFailureException 발생 (시도 " + (attempt + 1) + "): " + e.getMessage());
                        }
                    }
                    assertThat(success).isTrue(); // 최종적으로 성공했는지 확인
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Member updatedMember = memberService.getMember(member.getId());
        assertThat(updatedMember).isNotNull();
        // 추가적인 검증 로직 (예: 최종 업데이트된 멤버의 상태 확인)
    }


}


