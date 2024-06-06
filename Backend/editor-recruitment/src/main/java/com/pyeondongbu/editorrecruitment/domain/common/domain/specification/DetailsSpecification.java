package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.common.domain.Skill;
import com.pyeondongbu.editorrecruitment.domain.common.domain.VideoType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class DetailsSpecification { // 이거 인터페이스로 빼서 관리

    public static Specification<Details> equalMaxSubs(int maxSubs) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("maxSubs"), maxSubs);
    }

    public static Specification<Details> containsSkill(String skill) {
        return (root, query, criteriaBuilder) -> {
            Join<Details, Skill> skillsJoin = root.join("skills");
            return criteriaBuilder.equal(skillsJoin.get("name"), skill);
        };
    }

    public static Specification<Details> containsVideoType(String videoType) {
        return (root, query, criteriaBuilder) -> {
            Join<Details, VideoType> videoTypesJoin = root.join("videoTypes");
            return criteriaBuilder.equal(videoTypesJoin.get("name"), videoType);
        };
    }
}

