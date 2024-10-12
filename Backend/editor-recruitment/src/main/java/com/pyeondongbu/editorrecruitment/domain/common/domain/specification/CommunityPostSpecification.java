package com.pyeondongbu.editorrecruitment.domain.common.domain.specification;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CommunityPostSpecification {

    public static Specification<CommunityPost> withTitleOrContent(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) {
                return null;
            }
            String likePattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(root.get("content"), likePattern)
            );
        };
    }

    public static Specification<CommunityPost> withTags(List<String> tagNames) {
        return (root, query, cb) -> {
            if (tagNames == null || tagNames.isEmpty()) {
                return null;
            }
            query.distinct(true);
            Join<CommunityPost, Tag> tagJoin = root.join("tags", JoinType.INNER);
            return tagJoin.get("name").in(tagNames);
        };
    }

    public static Specification<CommunityPost> combineSpecifications(
            String search,
            List<String> tagNames
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                predicates.add(withTitleOrContent(search).toPredicate(root, query, cb));
            }

            if (tagNames != null && !tagNames.isEmpty()) {
                predicates.add(withTags(tagNames).toPredicate(root, query, cb));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}