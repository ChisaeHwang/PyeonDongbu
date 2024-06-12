package com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostDetailsReq;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@DiscriminatorValue("RECRUITMENT_POST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPostDetails extends Details {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "details")
    private RecruitmentPost recruitmentPost;

    @Builder
    public RecruitmentPostDetails(
            int maxSubs,
            String remarks,
            List<String> skills,
            List<String> videoTypes,
            RecruitmentPost recruitmentPost
    ) {
        super(maxSubs, remarks, skills, videoTypes);
        this.recruitmentPost = recruitmentPost;
    }

    public static RecruitmentPostDetails of(
            RecruitmentPost recruitmentPost,
            RecruitmentPostDetailsReq req
    ) {
        return RecruitmentPostDetails.builder()
                .recruitmentPost(recruitmentPost)
                .remarks(req.getRemarks())
                .maxSubs(req.getMaxSubs())
                .skills(req.getSkills())
                .videoTypes(req.getVideoTypes())
                .build();
    }

    public void setRecruitmentPost(RecruitmentPost recruitmentPost) {
        this.recruitmentPost = recruitmentPost;
    }
}

