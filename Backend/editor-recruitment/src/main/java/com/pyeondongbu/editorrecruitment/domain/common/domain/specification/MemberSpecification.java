package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

    public static Specification<Member> byDetails(Specification<MemberDetails> detailsSpec) {
        return (root, query, criteriaBuilder) -> {
            Join<Member, MemberDetails> detailsJoin = root.join("details");
            Root<MemberDetails> detailsRoot = query.from(MemberDetails.class);
            return detailsSpec.toPredicate(detailsRoot, query, criteriaBuilder);
        };
    }

    public static Specification<Member> hasRole(Role role) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role);
    }

    public static Specification<Member> hasNickname(String nickname) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nickname"), "%" + nickname + "%");
    }
}
