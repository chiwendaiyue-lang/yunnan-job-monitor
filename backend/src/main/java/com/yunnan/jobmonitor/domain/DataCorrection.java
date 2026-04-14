package com.yunnan.jobmonitor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "data_correction")
public class DataCorrection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "report_id")
  private EmploymentReport report;

  @Column(name = "operator_user_id", nullable = false)
  private Long operatorUserId;

  @Column(name = "field_name", nullable = false, length = 64)
  private String fieldName;

  @Column(name = "original_value", columnDefinition = "TEXT")
  private String originalValue;

  @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
  private String newValue;

  @Column(nullable = false, length = 512)
  private String reason;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}
