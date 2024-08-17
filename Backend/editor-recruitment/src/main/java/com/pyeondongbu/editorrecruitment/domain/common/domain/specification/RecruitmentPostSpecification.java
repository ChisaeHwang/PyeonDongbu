package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class RecruitmentPostSpecification {

    public static Specification<RecruitmentPost> containsTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<RecruitmentPost> containsSkills(List<String> skills) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details", JoinType.INNER);
            return detailsJoin.join("skills").in(skills);
        };
    }

    public static Specification<RecruitmentPost> containsVideoTypes(List<String> videoTypes) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details", JoinType.INNER);
            return detailsJoin.join("videoTypes").in(videoTypes);
        };
    }

    public static Specification<RecruitmentPost> withTags(List<String> tagNames) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<RecruitmentPost, Tag> tagJoin = root.join("tags", JoinType.INNER);
            return tagJoin.get("name").in(tagNames);
        };
    }

    public static Specification<RecruitmentPost> withMaxSubs(Integer maxSubs) {
        return (root, query, cb) -> {
            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details", JoinType.INNER);
            return cb.greaterThanOrEqualTo(detailsJoin.get("maxSubs"), maxSubs);
        };
    }

    public static Specification<RecruitmentPost> combineSpecifications(
            Integer maxSubs,
            String title,
            List<String> skills,
            List<String> videoTypes,
            List<String> tagNames
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details", JoinType.INNER);

            if (maxSubs != null) {
                predicates.add(cb.greaterThanOrEqualTo(detailsJoin.get("maxSubs"), maxSubs));
            }

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (skills != null && !skills.isEmpty()) {
                predicates.add(detailsJoin.join("skills").in(skills));
            }

            if (videoTypes != null && !videoTypes.isEmpty()) {
                predicates.add(detailsJoin.join("videoTypes").in(videoTypes));
            }

            if (tagNames != null && !tagNames.isEmpty()) {
                predicates.add(root.join("tags").get("name").in(tagNames));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
