package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class DetailsSpecification { // 이거 인터페이스로 빼서 나중에 관리 보류

    public static Specification<Details> equalMaxSubs(int maxSubs) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("maxSubs"), maxSubs);
    }

    public static Specification<Details> containsSkill(String skill) {
        return (root, query, criteriaBuilder) -> {
            Join<Details, String> skillsJoin = root.join("skills");
            return criteriaBuilder.equal(skillsJoin.get("name"), skill);
        };
    }

    public static Specification<Details> containsVideoType(String videoType) {
        return (root, query, criteriaBuilder) -> {
            Join<Details, String> videoTypesJoin = root.join("videoTypes");
            return criteriaBuilder.equal(videoTypesJoin.get("name"), videoType);
        };
    }
}

