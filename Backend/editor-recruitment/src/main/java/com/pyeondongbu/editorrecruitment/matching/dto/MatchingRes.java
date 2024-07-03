package com.pyeondongbu.editorrecruitment.matching.dto;

import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MatchingRes {

    private final List<MatchingResult> matchingResults;
    private final String message;

    public static MatchingRes from(
            final List<MatchingResult> matchingResults,
            final String message
    ) {
        return new MatchingRes(
                matchingResults,
                message
        );
    }


}
