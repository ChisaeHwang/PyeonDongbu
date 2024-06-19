package com.pyeondongbu.editorrecruitment.matching.domain;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Matcher {

    private final Vectorizer vectorizer;

    @Value("${recruitment.maxSubs.default}")
    private Double defaultMaxSubs;

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


            if (similarity > 0.1 && (postMaxSubs >= memberMaxSubs * defaultMaxSubs)) {
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
