package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.SurveyPeriod;
import com.yunnan.jobmonitor.service.SurveyPeriodService;
import com.yunnan.jobmonitor.web.dto.SurveyPeriodWriteDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SurveyPeriodController {

  private final SurveyPeriodService surveyPeriodService;

  public SurveyPeriodController(SurveyPeriodService surveyPeriodService) {
    this.surveyPeriodService = surveyPeriodService;
  }

  @GetMapping("/survey-periods")
  public List<SurveyPeriod> list() {
    return surveyPeriodService.listAll();
  }

  @PostMapping("/province/survey-periods")
  public SurveyPeriod create(@Valid @RequestBody SurveyPeriodWriteDto dto) {
    return surveyPeriodService.create(dto);
  }

  @PutMapping("/province/survey-periods/{id}")
  public SurveyPeriod update(@PathVariable Long id, @Valid @RequestBody SurveyPeriodWriteDto dto) {
    return surveyPeriodService.update(id, dto);
  }
}
