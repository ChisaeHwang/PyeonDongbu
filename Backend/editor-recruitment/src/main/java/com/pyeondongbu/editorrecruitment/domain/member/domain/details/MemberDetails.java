package com.pyeondongbu.editorrecruitment.domain.member.domain.details;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@DiscriminatorValue("MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetails extends Details {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "details")
    private Member member;

    @ElementCollection
    @Column(name = "edited_channel_name")
    private List<String> editedChannels;

    @ElementCollection
    @Column(name = "current_channel_name")
    private List<String> currentChannels;

    @Column
    private String portfolio;

    @Builder
    public MemberDetails(
            int maxSubs,
            String remarks,
            List<String> skills,
            List<String> videoTypes,
            List<String> editedChannels,
            List<String> currentChannels,
            String portfolio,
            Member member
    ) {
        super(maxSubs, remarks, skills, videoTypes);
        this.editedChannels = editedChannels;
        this.currentChannels = currentChannels;
        this.portfolio = portfolio;
        this.member = member;
    }

    public static MemberDetails of(
            Member member,
            MemberDetailsReq req
    ) {
        return MemberDetails.builder()
                .member(member)
                .remarks(req.getRemarks())
                .maxSubs(req.getMaxSubs())
                .editedChannels(req.getEditedChannels())
                .currentChannels(req.getCurrentChannels())
                .portfolio(req.getPortfolio())
                .skills(req.getSkills())
                .videoTypes(req.getVideoTypes())
                .build();
    }
}
