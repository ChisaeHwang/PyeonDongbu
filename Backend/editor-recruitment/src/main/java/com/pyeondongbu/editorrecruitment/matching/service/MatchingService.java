package com.pyeondongbu.editorrecruitment.matching.service;

import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import com.pyeondongbu.editorrecruitment.matching.dto.MatchingRes;

import java.util.*;
public interface MatchingService {

    MatchingRes findMatchingPosts(Long memberId);
}
