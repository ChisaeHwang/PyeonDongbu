package com.pyeondongbu.editorrecruitment.matching.domain;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class Matcher {

    private final Vectorizer vectorizer;

    public Matcher(Vectorizer vectorizer) {
        this.vectorizer = vectorizer;
    }

    public List<MatchingResult> match(Member member, List<RecruitmentPost> posts) {
        List<MatchingResult> results = new ArrayList<>();
        double[] memberVector = vectorizer.vectorize(member.getDetails());
        double memberMaxSubs = member.getDetails().getMaxSubs();

        for (RecruitmentPost post : posts) {
            double postMaxSubs = post.getDetails().getMaxSubs();
            double[] postVector = vectorizer.vectorize(post.getDetails());
            double similarity = CosineSimilarity.compute(memberVector, postVector);


            if (similarity > 0.1 && (postMaxSubs >= memberMaxSubs * 0.25)) {
                results.add(new MatchingResult(
                        MyPageRes.from(member),
                        RecruitmentPostRes.from(post),
                        similarity
                ));
            }
        }

        results.sort((r1, r2) -> Double.compare(r2.getSimilarity(), r1.getSimilarity()));  // 유사도 기준으로 정렬
        return results;
    }
}
