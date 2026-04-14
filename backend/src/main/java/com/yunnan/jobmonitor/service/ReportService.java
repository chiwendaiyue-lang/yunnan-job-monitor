package com.yunnan.jobmonitor.service;

import com.yunnan.jobmonitor.domain.DataCorrection;
import com.yunnan.jobmonitor.domain.EmploymentReport;
import com.yunnan.jobmonitor.domain.Enterprise;
import com.yunnan.jobmonitor.domain.FilingStatus;
import com.yunnan.jobmonitor.domain.ReportStatus;
import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.domain.SurveyPeriod;
import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.DataCorrectionRepository;
import com.yunnan.jobmonitor.repo.EmploymentReportRepository;
import com.yunnan.jobmonitor.repo.EnterpriseRepository;
import com.yunnan.jobmonitor.repo.SurveyPeriodRepository;
import com.yunnan.jobmonitor.web.dto.CorrectionRequest;
import com.yunnan.jobmonitor.web.dto.ReportWriteDto;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

  private static final Set<String> REDUCE_TYPES =
      Set.of(
          "关闭破产",
          "停业整顿",
          "经济性裁员",
          "业务转移",
          "自然减员",
          "正常解除或终止劳动合同",
          "国际因素变化影响",
          "自然灾害",
          "重大事件影响",
          "其他");

  private final EmploymentReportRepository reportRepository;
  private final SurveyPeriodRepository surveyPeriodRepository;
  private final EnterpriseRepository enterpriseRepository;
  private final CurrentUserService currentUserService;
  private final AuditService auditService;
  private final DataCorrectionRepository correctionRepository;

  public ReportService(
      EmploymentReportRepository reportRepository,
      SurveyPeriodRepository surveyPeriodRepository,
      EnterpriseRepository enterpriseRepository,
      CurrentUserService currentUserService,
      AuditService auditService,
      DataCorrectionRepository correctionRepository) {
    this.reportRepository = reportRepository;
    this.surveyPeriodRepository = surveyPeriodRepository;
    this.enterpriseRepository = enterpriseRepository;
    this.currentUserService = currentUserService;
    this.auditService = auditService;
    this.correctionRepository = correctionRepository;
  }

  @Transactional(readOnly = true)
  public List<EmploymentReport> listMine() {
    SysUser u = currentUserService.requireUser();
    requireEnterprise(u);
    return reportRepository.listByEnterpriseWithDetails(u.getEnterpriseId());
  }

  @Transactional
  public EmploymentReport saveDraft(ReportWriteDto dto) {
    SysUser u = currentUserService.requireUser();
    requireEnterprise(u);
    Enterprise ent = requireEnterpriseEntity(u.getEnterpriseId());
    if (ent.getFilingStatus() != FilingStatus.APPROVED) {
      throw new BusinessException("备案未通过，无法填报");
    }
    SurveyPeriod p = surveyPeriodRepository.findById(dto.surveyPeriodId()).orElseThrow();
    EmploymentReport r =
        reportRepository
            .findByEnterpriseIdAndSurveyPeriodId(ent.getId(), p.getId())
            .orElseGet(
                () -> {
                  EmploymentReport x = new EmploymentReport();
                  x.setEnterprise(ent);
                  x.setSurveyPeriod(p);
                  return x;
                });
    if (r.getStatus() != ReportStatus.DRAFT
        && r.getStatus() != ReportStatus.RETURNED) {
      throw new BusinessException("当前状态不可保存草稿");
    }
    validate(dto);
    apply(r, dto);
    if (r.getStatus() == ReportStatus.RETURNED) {
      r.setStatus(ReportStatus.DRAFT);
      r.setReturnRemark(null);
      r.setReturnedAt(null);
      r.setReturnedByUserId(null);
    } else {
      r.setStatus(ReportStatus.DRAFT);
    }
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(
        u.getId(), "REPORT_SAVE", "EmploymentReport", saved.getId(), Map.of("period", p.getId()));
    return saved;
  }

  @Transactional
  public EmploymentReport submit(Long reportId) {
    SysUser u = currentUserService.requireUser();
    requireEnterprise(u);
    Enterprise ent = requireEnterpriseEntity(u.getEnterpriseId());
    if (ent.getFilingStatus() != FilingStatus.APPROVED) {
      throw new BusinessException("备案未通过，无法提交");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    assertOwned(ent, r);
    if (r.getStatus() != ReportStatus.DRAFT) {
      throw new BusinessException("仅草稿可提交");
    }
    SurveyPeriod p = r.getSurveyPeriod();
    Instant now = Instant.now();
    if (now.isBefore(p.getReportStart()) || now.isAfter(p.getReportEnd())) {
      throw new BusinessException("不在上报期内，无法提交");
    }
    ReportWriteDto dto =
        new ReportWriteDto(
            p.getId(),
            r.getArchiveEmployment(),
            r.getSurveyEmployment(),
            r.getReduceType(),
            r.getMainReason(),
            r.getMainReasonNote(),
            r.getSecondaryReason(),
            r.getSecondaryReasonNote(),
            r.getThirdReason(),
            r.getThirdReasonNote(),
            r.getOtherReason());
    validate(dto);
    r.setStatus(ReportStatus.SUBMITTED);
    r.setSubmittedAt(now);
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(u.getId(), "REPORT_SUBMIT", "EmploymentReport", saved.getId(), Map.of());
    return saved;
  }

  @Transactional(readOnly = true)
  public List<EmploymentReport> pendingForCity() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.CITY || u.getRegionCode() == null) {
      throw new BusinessException("仅市用户可查看待审列表");
    }
    return reportRepository.findPendingForCity(
        u.getRegionCode(), EnumSet.of(ReportStatus.SUBMITTED));
  }

  @Transactional
  public EmploymentReport cityApprove(Long reportId) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.CITY || u.getRegionCode() == null) {
      throw new BusinessException("仅市用户可审核");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    if (!u.getRegionCode().equals(r.getEnterprise().getRegionCity())) {
      throw new BusinessException("非本市企业数据");
    }
    if (r.getStatus() != ReportStatus.SUBMITTED) {
      throw new BusinessException("当前状态不可市级通过");
    }
    r.setStatus(ReportStatus.CITY_APPROVED);
    r.setCityReviewedAt(Instant.now());
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(u.getId(), "CITY_APPROVE", "EmploymentReport", saved.getId(), Map.of());
    return saved;
  }

  @Transactional
  public EmploymentReport cityReturn(Long reportId, String remark) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.CITY || u.getRegionCode() == null) {
      throw new BusinessException("仅市用户可操作退回");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    if (!u.getRegionCode().equals(r.getEnterprise().getRegionCity())) {
      throw new BusinessException("非本市企业数据");
    }
    if (r.getStatus() != ReportStatus.SUBMITTED) {
      throw new BusinessException("当前状态不可退回");
    }
    applyReturn(r, u, remark);
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(
        u.getId(),
        "CITY_RETURN",
        "EmploymentReport",
        saved.getId(),
        Map.of("remark", remark == null ? "" : remark));
    return saved;
  }

  @Transactional(readOnly = true)
  public List<EmploymentReport> pendingForProvince() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可查看");
    }
    return reportRepository.findAllWithDetails().stream()
        .filter(r -> r.getStatus() == ReportStatus.CITY_APPROVED)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<EmploymentReport> allReports() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可查看");
    }
    return reportRepository.findAllWithDetails();
  }

  @Transactional
  public EmploymentReport provinceApprove(Long reportId) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可终审");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    if (r.getStatus() != ReportStatus.CITY_APPROVED) {
      throw new BusinessException("当前状态不可省级通过");
    }
    r.setStatus(ReportStatus.PROVINCE_APPROVED);
    r.setProvinceReviewedAt(Instant.now());
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(u.getId(), "PROVINCE_APPROVE", "EmploymentReport", saved.getId(), Map.of());
    return saved;
  }

  @Transactional
  public EmploymentReport provinceReturn(Long reportId, String remark) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可退回");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    if (r.getStatus() != ReportStatus.CITY_APPROVED) {
      throw new BusinessException("当前状态不可省级退回");
    }
    applyReturn(r, u, remark);
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(
        u.getId(),
        "PROVINCE_RETURN",
        "EmploymentReport",
        saved.getId(),
        Map.of("remark", remark == null ? "" : remark));
    return saved;
  }

  @Transactional
  public EmploymentReport markNational(Long reportId) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可标记上报");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    if (r.getStatus() != ReportStatus.PROVINCE_APPROVED) {
      throw new BusinessException("仅已通过数据可标记部级上报");
    }
    r.setReportedToNational(true);
    EmploymentReport saved = reportRepository.save(r);
    auditService.log(u.getId(), "REPORT_NATIONAL", "EmploymentReport", saved.getId(), Map.of());
    return saved;
  }

  @Transactional
  public DataCorrection correct(Long reportId, CorrectionRequest req) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可修正数据");
    }
    EmploymentReport r = reportRepository.findById(reportId).orElseThrow();
    if (r.getStatus() != ReportStatus.PROVINCE_APPROVED) {
      throw new BusinessException("仅已通过数据可修正");
    }
    String field = req.fieldName();
    String original = readField(r, field);
    applyField(r, field, req.newValue());
    reportRepository.save(r);
    DataCorrection c = new DataCorrection();
    c.setReport(r);
    c.setOperatorUserId(u.getId());
    c.setFieldName(field);
    c.setOriginalValue(original);
    c.setNewValue(req.newValue());
    c.setReason(req.reason());
    DataCorrection saved = correctionRepository.save(c);
    auditService.log(
        u.getId(),
        "CORRECTION_CREATE",
        "DataCorrection",
        saved.getId(),
        Map.of("reportId", reportId, "field", field));
    return saved;
  }

  private void applyReturn(EmploymentReport r, SysUser u, String remark) {
    r.setStatus(ReportStatus.RETURNED);
    r.setReturnRemark(remark);
    r.setReturnedAt(Instant.now());
    r.setReturnedByUserId(u.getId());
  }

  private void requireEnterprise(SysUser u) {
    if (u.getRole().getCode() != RoleCode.ENTERPRISE || u.getEnterpriseId() == null) {
      throw new BusinessException("仅企业用户可操作");
    }
  }

  private Enterprise requireEnterpriseEntity(Long enterpriseId) {
    return enterpriseRepository
        .findById(enterpriseId)
        .orElseThrow(() -> new BusinessException("企业不存在"));
  }

  private void assertOwned(Enterprise ent, EmploymentReport r) {
    if (!r.getEnterprise().getId().equals(ent.getId())) {
      throw new BusinessException("数据不属于当前企业");
    }
  }

  private void validate(ReportWriteDto dto) {
    if (dto.surveyEmployment() < dto.archiveEmployment()) {
      if (dto.reduceType() == null
          || dto.reduceType().isBlank()
          || dto.mainReason() == null
          || dto.mainReason().isBlank()
          || dto.mainReasonNote() == null
          || dto.mainReasonNote().isBlank()) {
        throw new BusinessException("调查期就业人数小于建档期时，减少类型与主要原因及说明必填");
      }
      if (!REDUCE_TYPES.contains(dto.reduceType())) {
        throw new BusinessException("就业人数减少类型不在允许范围内");
      }
    }
  }

  private void apply(EmploymentReport r, ReportWriteDto dto) {
    r.setArchiveEmployment(dto.archiveEmployment());
    r.setSurveyEmployment(dto.surveyEmployment());
    r.setReduceType(dto.reduceType());
    r.setMainReason(dto.mainReason());
    r.setMainReasonNote(dto.mainReasonNote());
    r.setSecondaryReason(dto.secondaryReason());
    r.setSecondaryReasonNote(dto.secondaryReasonNote());
    r.setThirdReason(dto.thirdReason());
    r.setThirdReasonNote(dto.thirdReasonNote());
    r.setOtherReason(dto.otherReason());
  }

  private String readField(EmploymentReport r, String field) {
    return switch (field) {
      case "archiveEmployment" -> String.valueOf(r.getArchiveEmployment());
      case "surveyEmployment" -> String.valueOf(r.getSurveyEmployment());
      case "otherReason" -> r.getOtherReason();
      default -> throw new BusinessException("不支持修正的字段: " + field);
    };
  }

  private void applyField(EmploymentReport r, String field, String value) {
    switch (field) {
      case "archiveEmployment" -> r.setArchiveEmployment(Integer.parseInt(value));
      case "surveyEmployment" -> r.setSurveyEmployment(Integer.parseInt(value));
      case "otherReason" -> r.setOtherReason(value);
      default -> throw new BusinessException("不支持修正的字段: " + field);
    }
  }
}
