package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class RecruitmentPostDetailsSpecification {

    public static Specification<RecruitmentPostDetails> equalMaxSubs(int maxSubs) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("maxSubs"), maxSubs);
    }

    public static Specification<RecruitmentPostDetails> containsSkill(String skill) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(skill, root.get("skills"));
    }

    public static Specification<RecruitmentPostDetails> containsVideoType(String videoType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(videoType, root.get("videoTypes"));
    }
}