package com.pyeondongbu.editorrecruitment.domain.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Details {

    @Min(0)
    @Max(10000000)
    @Column(nullable = false)
    protected int maxSubs;

    @Column(length = 1000)
    protected String remarks;

    @ElementCollection
    @Column(name = "skill")
    protected List<String> skills;

    @ElementCollection
    @Column(name = "video_type")
    protected List<String> videoTypes;

    public Details(
            int maxSubs,
            String remarks,
            List<String> skills,
            List<String> videoTypes
    ) {
        this.maxSubs = maxSubs;
        this.remarks = remarks;
        this.skills = skills;
        this.videoTypes = videoTypes;
    }
}
