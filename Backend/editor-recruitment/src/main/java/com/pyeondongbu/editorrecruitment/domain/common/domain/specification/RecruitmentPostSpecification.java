package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecruitmentPostSpecification {

    public static Specification<RecruitmentPost> containsTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<RecruitmentPost> containsSkills(List<String> skills) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("skills").get("name").in(skills);
        };
    }

    public static Specification<RecruitmentPost> containsVideoTypes(List<String> videoTypes) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("videoTypes").get("name").in(videoTypes);
        };
    }

    public static Specification<RecruitmentPost> withTags(List<String> tagNames) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("tags").get("name").in(tagNames);
        };
    }

    public static Specification<RecruitmentPost> withMaxSubs(Integer maxSubs) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("subscriptions"), maxSubs);
    }
}

