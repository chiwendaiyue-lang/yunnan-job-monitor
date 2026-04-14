package com.yunnan.jobmonitor.web.dto;

import com.yunnan.jobmonitor.domain.ReportStatus;
import java.time.Instant;

public record ReportResponse(
    Long id,
    Long enterpriseId,
    String enterpriseName,
    Long surveyPeriodId,
    String surveyPeriodName,
    Integer archiveEmployment,
    Integer surveyEmployment,
    String reduceType,
    String mainReason,
    String mainReasonNote,
    String secondaryReason,
    String secondaryReasonNote,
    String thirdReason,
    String thirdReasonNote,
    String otherReason,
    ReportStatus status,
    String returnRemark,
    Instant returnedAt,
    Instant submittedAt,
    Instant cityReviewedAt,
    Instant provinceReviewedAt,
    boolean reportedToNational) {}
