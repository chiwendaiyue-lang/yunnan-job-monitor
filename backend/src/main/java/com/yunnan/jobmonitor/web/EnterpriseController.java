package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.Enterprise;
import com.yunnan.jobmonitor.domain.FilingStatus;
import com.yunnan.jobmonitor.service.EnterpriseService;
import com.yunnan.jobmonitor.web.dto.EnterpriseResponse;
import com.yunnan.jobmonitor.web.dto.EnterpriseWriteDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EnterpriseController {

  private final EnterpriseService enterpriseService;

  public EnterpriseController(EnterpriseService enterpriseService) {
    this.enterpriseService = enterpriseService;
  }

  @GetMapping("/enterprise/me")
  public EnterpriseResponse myEnterprise() {
    return WebMapper.toEnterprise(enterpriseService.myEnterprise());
  }

  @PutMapping("/enterprise/me")
  public EnterpriseResponse updateMine(@Valid @RequestBody EnterpriseWriteDto dto) {
    return WebMapper.toEnterprise(enterpriseService.saveMine(dto));
  }

  @PostMapping("/enterprise/me/submit-filing")
  public EnterpriseResponse submitFiling() {
    return WebMapper.toEnterprise(enterpriseService.submitFiling());
  }

  @GetMapping("/province/enterprises")
  public List<EnterpriseResponse> listProvince(
      @RequestParam(required = false) String regionCity,
      @RequestParam(required = false) FilingStatus filingStatus) {
    return enterpriseService.listForProvince(regionCity, filingStatus).stream()
        .map(WebMapper::toEnterprise)
        .toList();
  }

  @PostMapping("/province/enterprises/{id}/approve-filing")
  public void approveFiling(@PathVariable Long id) {
    enterpriseService.approveFiling(id);
  }

  @GetMapping("/province/summary/by-city")
  public List<java.util.Map<String, Object>> summaryByCity() {
    return enterpriseService.summarizeByCity();
  }
}
