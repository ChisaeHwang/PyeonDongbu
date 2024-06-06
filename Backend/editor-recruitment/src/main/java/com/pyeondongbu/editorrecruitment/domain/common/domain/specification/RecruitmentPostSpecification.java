package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class RecruitmentPostSpecification {

    public static Specification<RecruitmentPost> byDetails(Specification<RecruitmentPostDetails> detailsSpec) {
        return (root, query, criteriaBuilder) -> {
            Join<RecruitmentPost, RecruitmentPostDetails> detailsJoin = root.join("details");
            Root<RecruitmentPostDetails> detailsRoot = query.from(RecruitmentPostDetails.class);
            return detailsSpec.toPredicate(detailsRoot, query, criteriaBuilder);
        };
    }

    public static Specification<RecruitmentPost> withTag(String tagName) {
        return (root, query, criteriaBuilder) -> {
            Join<RecruitmentPost, Tag> tags = root.join("tags");
            return criteriaBuilder.equal(tags.get("name"), tagName);
        };
    }
}

