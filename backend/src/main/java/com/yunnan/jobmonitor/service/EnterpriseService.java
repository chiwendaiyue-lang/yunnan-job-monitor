package com.yunnan.jobmonitor.service;

import com.yunnan.jobmonitor.domain.Enterprise;
import com.yunnan.jobmonitor.domain.FilingStatus;
import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.EnterpriseRepository;
import com.yunnan.jobmonitor.web.dto.EnterpriseWriteDto;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnterpriseService {

  private final EnterpriseRepository enterpriseRepository;
  private final CurrentUserService currentUserService;
  private final AuditService auditService;

  public EnterpriseService(
      EnterpriseRepository enterpriseRepository,
      CurrentUserService currentUserService,
      AuditService auditService) {
    this.enterpriseRepository = enterpriseRepository;
    this.currentUserService = currentUserService;
    this.auditService = auditService;
  }

  @Transactional(readOnly = true)
  public Enterprise myEnterprise() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.ENTERPRISE || u.getEnterpriseId() == null) {
      throw new BusinessException("仅企业用户可访问");
    }
    return enterpriseRepository.findById(u.getEnterpriseId()).orElseThrow();
  }

  @Transactional
  public Enterprise saveMine(EnterpriseWriteDto dto) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.ENTERPRISE || u.getEnterpriseId() == null) {
      throw new BusinessException("仅企业用户可维护备案信息");
    }
    Enterprise e = enterpriseRepository.findById(u.getEnterpriseId()).orElseThrow();
    if (e.getFilingStatus() == FilingStatus.APPROVED) {
      throw new BusinessException("备案已通过，信息不可修改");
    }
    apply(e, dto);
    Enterprise saved = enterpriseRepository.save(e);
    auditService.log(
        u.getId(),
        "FILING_SAVE",
        "Enterprise",
        saved.getId(),
        Map.of("orgCode", saved.getOrgCode(), "name", saved.getName()));
    return saved;
  }

  @Transactional
  public Enterprise submitFiling() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.ENTERPRISE || u.getEnterpriseId() == null) {
      throw new BusinessException("仅企业用户可提交备案");
    }
    Enterprise e = enterpriseRepository.findById(u.getEnterpriseId()).orElseThrow();
    if (e.getFilingStatus() == FilingStatus.APPROVED) {
      throw new BusinessException("备案已通过");
    }
    e.setFilingStatus(FilingStatus.SUBMITTED);
    Enterprise saved = enterpriseRepository.save(e);
    auditService.log(u.getId(), "FILING_SUBMIT", "Enterprise", saved.getId(), Map.of());
    return saved;
  }

  @Transactional
  public void approveFiling(Long enterpriseId) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可审核备案");
    }
    Enterprise e = enterpriseRepository.findById(enterpriseId).orElseThrow();
    if (e.getFilingStatus() != FilingStatus.SUBMITTED) {
      throw new BusinessException("当前状态不可审核通过");
    }
    e.setFilingStatus(FilingStatus.APPROVED);
    enterpriseRepository.save(e);
    auditService.log(u.getId(), "FILING_APPROVE", "Enterprise", enterpriseId, Map.of());
  }

  @Transactional(readOnly = true)
  public List<Enterprise> listForProvince(String regionCity, FilingStatus filingStatus) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可查询");
    }
    List<Enterprise> all = enterpriseRepository.findAll();
    return all.stream()
        .filter(e -> regionCity == null || regionCity.isBlank() || regionCity.equals(e.getRegionCity()))
        .filter(e -> filingStatus == null || e.getFilingStatus() == filingStatus)
        .toList();
  }

  private void apply(Enterprise e, EnterpriseWriteDto dto) {
    e.setRegionCity(dto.regionCity());
    e.setRegionCounty(dto.regionCounty());
    e.setRegionArea(dto.regionArea());
    e.setOrgCode(dto.orgCode());
    e.setName(dto.name());
    e.setNatureLevel1(dto.natureLevel1());
    e.setNatureLevel2(dto.natureLevel2());
    e.setIndustryLevel1(dto.industryLevel1());
    e.setIndustryLevel2(dto.industryLevel2());
    e.setMainBusiness(dto.mainBusiness());
    e.setContactName(dto.contactName());
    e.setAddressLevel1(dto.addressLevel1());
    e.setAddressLevel2(dto.addressLevel2());
    e.setAddressDetail(dto.addressDetail());
    e.setPostalCode(dto.postalCode());
    e.setPhone(dto.phone());
    e.setFax(dto.fax());
    e.setEmail(dto.email());
  }

  @Transactional(readOnly = true)
  public List<Map<String, Object>> summarizeByCity() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可查看汇总");
    }
    List<Enterprise> all = enterpriseRepository.findAll();
    Map<String, Long> counts = new HashMap<>();
    for (Enterprise e : all) {
      if (e.getFilingStatus() != FilingStatus.APPROVED) {
        continue;
      }
      counts.merge(e.getRegionCity(), 1L, Long::sum);
    }
    long total = counts.values().stream().mapToLong(Long::longValue).sum();
    return counts.entrySet().stream()
        .map(
            en -> {
              Map<String, Object> row = new HashMap<>();
              row.put("regionCity", en.getKey());
              row.put("count", en.getValue());
              row.put("ratio", total == 0 ? 0.0 : en.getValue() * 1.0 / total);
              return row;
            })
        .toList();
  }
}
