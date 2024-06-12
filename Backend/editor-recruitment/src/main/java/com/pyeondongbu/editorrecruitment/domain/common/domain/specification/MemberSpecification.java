package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class MemberSpecification {

    public static Specification<Member> hasMaxSubs(Integer maxSubs) {
        return (root, query, cb) -> {
            Join<Member, MemberDetails> detailsJoin = root.join("details", JoinType.INNER);
            return cb.greaterThanOrEqualTo(detailsJoin.get("maxSubs"), maxSubs);
        };
    }

    public static Specification<Member> hasRole(Role role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<Member> hasSkills(List<String> skills) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Member, MemberDetails> detailsJoin = root.join("details", JoinType.INNER);
            return detailsJoin.join("skills").in(skills);
        };
    }

    public static Specification<Member> hasVideoTypes(List<String> videoTypes) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Member, MemberDetails> detailsJoin = root.join("details", JoinType.INNER);
            return detailsJoin.join("videoTypes").in(videoTypes);
        };
    }

    public static Specification<Member> combineSpecifications(
            Integer maxSubs,
            Role role,
            List<String> skills,
            List<String> videoTypes
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (maxSubs != null) {
                Join<Member, MemberDetails> detailsJoin = root.join("details", JoinType.INNER);
                predicates.add(cb.greaterThanOrEqualTo(detailsJoin.get("maxSubs"), maxSubs));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            if (skills != null && !skills.isEmpty()) {
                Join<Member, MemberDetails> detailsJoin = root.join("details", JoinType.INNER);
                predicates.add(detailsJoin.join("skills").in(skills));
            }

            if (videoTypes != null && !videoTypes.isEmpty()) {
                Join<Member, MemberDetails> detailsJoin = root.join("details", JoinType.INNER);
                predicates.add(detailsJoin.join("videoTypes").in(videoTypes));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
