package com.yunnan.jobmonitor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employment_report")
public class EmploymentReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "enterprise_id")
  private Enterprise enterprise;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "survey_period_id")
  private SurveyPeriod surveyPeriod;

  @Column(name = "archive_employment", nullable = false)
  private Integer archiveEmployment;

  @Column(name = "survey_employment", nullable = false)
  private Integer surveyEmployment;

  @Column(name = "reduce_type", length = 128)
  private String reduceType;

  @Column(name = "main_reason", length = 128)
  private String mainReason;

  @Column(name = "main_reason_note", length = 512)
  private String mainReasonNote;

  @Column(name = "secondary_reason", length = 128)
  private String secondaryReason;

  @Column(name = "secondary_reason_note", length = 512)
  private String secondaryReasonNote;

  @Column(name = "third_reason", length = 128)
  private String thirdReason;

  @Column(name = "third_reason_note", length = 512)
  private String thirdReasonNote;

  @Column(name = "other_reason", nullable = false, length = 512)
  private String otherReason = "";

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ReportStatus status = ReportStatus.DRAFT;

  @Column(name = "return_remark", length = 1024)
  private String returnRemark;

  @Column(name = "returned_at")
  private Instant returnedAt;

  @Column(name = "returned_by_user_id")
  private Long returnedByUserId;

  @Column(name = "submitted_at")
  private Instant submittedAt;

  @Column(name = "city_reviewed_at")
  private Instant cityReviewedAt;

  @Column(name = "province_reviewed_at")
  private Instant provinceReviewedAt;

  @Column(name = "reported_to_national", nullable = false)
  private boolean reportedToNational = false;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();
}
