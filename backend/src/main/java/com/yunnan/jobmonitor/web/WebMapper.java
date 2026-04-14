package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.EmploymentReport;
import com.yunnan.jobmonitor.domain.Enterprise;
import com.yunnan.jobmonitor.web.dto.EnterpriseResponse;
import com.yunnan.jobmonitor.web.dto.ReportResponse;

public final class WebMapper {

  private WebMapper() {}

  public static EnterpriseResponse toEnterprise(Enterprise e) {
    return new EnterpriseResponse(
        e.getId(),
        e.getRegionProvince(),
        e.getRegionCity(),
        e.getRegionCounty(),
        e.getRegionArea(),
        e.getOrgCode(),
        e.getName(),
        e.getNatureLevel1(),
        e.getNatureLevel2(),
        e.getIndustryLevel1(),
        e.getIndustryLevel2(),
        e.getMainBusiness(),
        e.getContactName(),
        e.getAddressLevel1(),
        e.getAddressLevel2(),
        e.getAddressDetail(),
        e.getPostalCode(),
        e.getPhone(),
        e.getFax(),
        e.getEmail(),
        e.getFilingStatus());
  }

  public static ReportResponse toReport(EmploymentReport r) {
    return new ReportResponse(
        r.getId(),
        r.getEnterprise().getId(),
        r.getEnterprise().getName(),
        r.getSurveyPeriod().getId(),
        r.getSurveyPeriod().getName(),
        r.getArchiveEmployment(),
        r.getSurveyEmployment(),
        r.getReduceType(),
        r.getMainReason(),
        r.getMainReasonNote(),
        r.getSecondaryReason(),
        r.getSecondaryReasonNote(),
        r.getThirdReason(),
        r.getThirdReasonNote(),
        r.getOtherReason(),
        r.getStatus(),
        r.getReturnRemark(),
        r.getReturnedAt(),
        r.getSubmittedAt(),
        r.getCityReviewedAt(),
        r.getProvinceReviewedAt(),
        r.isReportedToNational());
  }
}
