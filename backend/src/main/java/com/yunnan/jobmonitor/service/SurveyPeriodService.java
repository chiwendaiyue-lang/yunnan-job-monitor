package com.yunnan.jobmonitor.service;

import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.domain.SurveyPeriod;
import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.SurveyPeriodRepository;
import com.yunnan.jobmonitor.web.dto.SurveyPeriodWriteDto;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SurveyPeriodService {

  private final SurveyPeriodRepository surveyPeriodRepository;
  private final CurrentUserService currentUserService;

  public SurveyPeriodService(
      SurveyPeriodRepository surveyPeriodRepository, CurrentUserService currentUserService) {
    this.surveyPeriodRepository = surveyPeriodRepository;
    this.currentUserService = currentUserService;
  }

  @Transactional(readOnly = true)
  public List<SurveyPeriod> listAll() {
    return surveyPeriodRepository.findAll();
  }

  @Transactional
  public SurveyPeriod create(SurveyPeriodWriteDto dto) {
    requireProvince();
    SurveyPeriod p = new SurveyPeriod();
    apply(p, dto);
    return surveyPeriodRepository.save(p);
  }

  @Transactional
  public SurveyPeriod update(Long id, SurveyPeriodWriteDto dto) {
    requireProvince();
    SurveyPeriod p = surveyPeriodRepository.findById(id).orElseThrow();
    apply(p, dto);
    return surveyPeriodRepository.save(p);
  }

  private void requireProvince() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可维护调查期");
    }
  }

  private void apply(SurveyPeriod p, SurveyPeriodWriteDto dto) {
    p.setName(dto.name());
    p.setReportStart(dto.reportStart());
    p.setReportEnd(dto.reportEnd());
    p.setActive(dto.active());
  }
}
