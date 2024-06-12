package com.pyeondongbu.editorrecruitment.matching.service;

import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import java.util.*;
public interface MatchingService {

    List<MatchingResult> findMatchingPosts(Long memberId);

    List<MatchingResult> findTopKMatchingPosts(Long memberId, int k);
}
