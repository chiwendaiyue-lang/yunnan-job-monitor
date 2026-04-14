package com.yunnan.jobmonitor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "enterprise")
public class Enterprise {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "region_province", nullable = false, length = 32)
  private String regionProvince = "530000";

  @Column(name = "region_city", nullable = false, length = 32)
  private String regionCity;

  @Column(name = "region_county", nullable = false, length = 32)
  private String regionCounty;

  @Column(name = "region_area", length = 128)
  private String regionArea;

  @Column(name = "org_code", nullable = false, unique = true, length = 9)
  private String orgCode;

  @Column(nullable = false, length = 256)
  private String name;

  @Column(name = "nature_level1", nullable = false, length = 64)
  private String natureLevel1;

  @Column(name = "nature_level2", nullable = false, length = 64)
  private String natureLevel2;

  @Column(name = "industry_level1", nullable = false, length = 64)
  private String industryLevel1;

  @Column(name = "industry_level2", nullable = false, length = 64)
  private String industryLevel2;

  @Column(name = "main_business", nullable = false, length = 512)
  private String mainBusiness;

  @Column(name = "contact_name", nullable = false, length = 128)
  private String contactName;

  @Column(name = "address_level1", nullable = false, length = 64)
  private String addressLevel1;

  @Column(name = "address_level2", nullable = false, length = 64)
  private String addressLevel2;

  @Column(name = "address_detail", nullable = false, length = 256)
  private String addressDetail;

  @Column(name = "postal_code", nullable = false, length = 6)
  private String postalCode;

  @Column(nullable = false, length = 64)
  private String phone;

  @Column(nullable = false, length = 64)
  private String fax;

  @Column(length = 128)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "filing_status", nullable = false, length = 32)
  private FilingStatus filingStatus = FilingStatus.DRAFT;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();
}
