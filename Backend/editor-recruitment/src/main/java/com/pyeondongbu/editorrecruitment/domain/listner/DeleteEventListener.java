package com.pyeondongbu.editorrecruitment.domain.listner;

import com.pyeondongbu.editorrecruitment.domain.member.domain.MemberDeleteEvent;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
public class DeleteEventListener {

    private final RecruitmentPostRepository postRepository;

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(fallbackExecution = true)
    public void deleteMember(final MemberDeleteEvent event) {

        postRepository.deleteByMemberId(event.getMemberId());
    }



}
