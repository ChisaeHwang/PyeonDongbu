package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class RecruitmentPostSpecification {

    public static Specification<RecruitmentPost> containsTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<RecruitmentPost> withMaxSubs(Integer maxSubs) {
        return (root, query, cb) -> {
            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details", JoinType.LEFT);
            return cb.greaterThanOrEqualTo(detailsJoin.get("maxSubs"), maxSubs);
        };
    }

    public static Specification<RecruitmentPost> withTags(List<String> tagNames) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<RecruitmentPost, Tag> tagJoin = root.join("tags", JoinType.INNER);
            return tagJoin.get("name").in(tagNames);
        };
    }

    public static Specification<RecruitmentPost> combineSpecifications(
            String title,
            String maxSubs,
            PaymentType paymentType,
            String workload,
            List<String> skills,
            List<String> videoTypes,
            List<String> tagNames
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 중복 제거를 위해 distinct 사용
            query.distinct(true);

            // 조인 최적화
            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details", JoinType.LEFT);
            Join<RecruitmentPost, Payment> paymentJoin = root.join("payment", JoinType.LEFT);

            if (maxSubs != null) {
                switch(maxSubs) {
                    case "0-1":
                        predicates.add(cb.lessThanOrEqualTo(detailsJoin.get("maxSubs"), 10000));
                        break;
                    case "1-5":
                        predicates.add(cb.between(detailsJoin.get("maxSubs"), 10001, 50000));
                        break;
                    case "5-10":
                        predicates.add(cb.between(detailsJoin.get("maxSubs"), 50000, 100000));
                        break;
                    case "10-50":
                        predicates.add(cb.between(detailsJoin.get("maxSubs"), 100000, 500000));
                        break;
                    case "50-100":
                        predicates.add(cb.between(detailsJoin.get("maxSubs"), 500000, 1000000));
                        break;
                    case "100+":
                        predicates.add(cb.greaterThan(detailsJoin.get("maxSubs"), 1000000));
                        break;
                }
            }

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (workload != null) {
                switch(workload) {
                    case "1-2":
                        predicates.add(cb.or(
                                cb.equal(detailsJoin.get("weeklyWorkload"), 1),
                                cb.equal(detailsJoin.get("weeklyWorkload"), 2)
                        ));
                        break;
                    case "3-4":
                        predicates.add(cb.or(
                                cb.equal(detailsJoin.get("weeklyWorkload"), 3),
                                cb.equal(detailsJoin.get("weeklyWorkload"), 4)
                        ));
                        break;
                    case "5+":
                        predicates.add(cb.greaterThanOrEqualTo(detailsJoin.get("weeklyWorkload"), 5));
                        break;
                }
            }

            if (paymentType != null) {
                predicates.add(cb.equal(paymentJoin.get("type"), paymentType));
            }

            // 서브쿼리를 사용하여 skills, videoTypes, tagNames 처리
            if (skills != null && !skills.isEmpty()) {
                Subquery<Long> skillSubquery = query.subquery(Long.class);
                Root<RecruitmentPost> subRoot = skillSubquery.from(RecruitmentPost.class);
                skillSubquery.select(subRoot.get("id"))
                        .where(subRoot.join("details").join("skills").in(skills));
                predicates.add(root.get("id").in(skillSubquery));
            }

            if (videoTypes != null && !videoTypes.isEmpty()) {
                Subquery<Long> videoTypeSubquery = query.subquery(Long.class);
                Root<RecruitmentPost> subRoot = videoTypeSubquery.from(RecruitmentPost.class);
                videoTypeSubquery.select(subRoot.get("id"))
                        .where(subRoot.join("details").join("videoTypes").in(videoTypes));
                predicates.add(root.get("id").in(videoTypeSubquery));
            }

            if (tagNames != null && !tagNames.isEmpty()) {
                Subquery<Long> tagSubquery = query.subquery(Long.class);
                Root<RecruitmentPost> subRoot = tagSubquery.from(RecruitmentPost.class);
                tagSubquery.select(subRoot.get("id"))
                        .where(subRoot.join("tags").get("name").in(tagNames));
                predicates.add(root.get("id").in(tagSubquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}