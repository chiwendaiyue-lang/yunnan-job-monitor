package com.yunnan.jobmonitor.web.dto;

import com.yunnan.jobmonitor.domain.FilingStatus;

public record EnterpriseResponse(
    Long id,
    String regionProvince,
    String regionCity,
    String regionCounty,
    String regionArea,
    String orgCode,
    String name,
    String natureLevel1,
    String natureLevel2,
    String industryLevel1,
    String industryLevel2,
    String mainBusiness,
    String contactName,
    String addressLevel1,
    String addressLevel2,
    String addressDetail,
    String postalCode,
    String phone,
    String fax,
    String email,
    FilingStatus filingStatus) {}
