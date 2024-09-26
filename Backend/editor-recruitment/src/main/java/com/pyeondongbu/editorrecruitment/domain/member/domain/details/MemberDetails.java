package com.pyeondongbu.editorrecruitment.domain.member.domain.details;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    private Set<String> editedChannels;

    @ElementCollection
    @Column(name = "current_channel_name")
    private Set<String> currentChannels;

    @Column
    private String portfolio;

    @Builder
    public MemberDetails(
            int maxSubs,
            int weeklyWorkload,
            String remarks,
            Set<String> skills,
            Set<String> videoTypes,
            Set<String> editedChannels,
            Set<String> currentChannels,
            String portfolio,
            Member member
    ) {
        super(maxSubs, weeklyWorkload, remarks, skills, videoTypes);
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
                .weeklyWorkload(req.getWeeklyWorkload())
                .editedChannels(req.getEditedChannels())
                .currentChannels(req.getCurrentChannels())
                .portfolio(req.getPortfolio())
                .skills(req.getSkills())
                .videoTypes(req.getVideoTypes())
                .build();
    }

    public void update(MemberDetailsReq req) {
        this.maxSubs = req.getMaxSubs();
        this.weeklyWorkload = req.getWeeklyWorkload();
        this.remarks = req.getRemarks();
        this.skills = req.getSkills();
        this.videoTypes = req.getVideoTypes();
        this.editedChannels = req.getEditedChannels();
        this.currentChannels = req.getCurrentChannels();
        this.portfolio = req.getPortfolio();
    }
}
