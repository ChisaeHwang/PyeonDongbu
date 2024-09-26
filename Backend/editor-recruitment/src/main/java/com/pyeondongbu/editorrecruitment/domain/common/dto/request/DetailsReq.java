package com.pyeondongbu.editorrecruitment.domain.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DetailsReq {
    @Min(value = 0, message = "구독자 수는 0 이상이어야 합니다.")
    @Max(value = 10000000, message = "구독자 수는 1천만 이하이어야 합니다.")
    private int maxSubs;

    @Min(value = 0, message = "주간 작업 갯수는 0 이상이어야 합니다.")
    @Max(value = 10, message = "주간 작업 갯수는 10개 이하이어야 합니다.")
    private int weeklyWorkload;

    @NotNull(message = "장르는 공백이 될 수 없습니다.")
    private Set<String> videoTypes;

    @NotNull(message = "스킬 리스트는 필수입니다.")
    @Size(min = 1, message = "최소한 하나 이상의 스킬이 필요합니다.")
    private Set<String> skills;

    @Size(max = 1000, message = "비고란은 1000자를 초과할 수 없습니다.")
    private String remarks;



}