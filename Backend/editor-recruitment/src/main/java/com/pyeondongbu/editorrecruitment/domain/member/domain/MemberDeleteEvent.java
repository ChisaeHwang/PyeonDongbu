package com.pyeondongbu.editorrecruitment.domain.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberDeleteEvent {

    private final List<Long> postIds;
    private final Long memberId;
}
