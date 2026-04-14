package com.yunnan.jobmonitor.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportWriteDto(
    @NotNull Long surveyPeriodId,
    @NotNull @Min(0) Integer archiveEmployment,
    @NotNull @Min(0) Integer surveyEmployment,
    String reduceType,
    String mainReason,
    String mainReasonNote,
    String secondaryReason,
    String secondaryReasonNote,
    String thirdReason,
    String thirdReasonNote,
    @NotBlank String otherReason) {}
