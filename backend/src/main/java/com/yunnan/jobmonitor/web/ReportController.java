package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.service.ReportService;
import com.yunnan.jobmonitor.web.dto.CorrectionRequest;
import com.yunnan.jobmonitor.web.dto.RemarkRequest;
import com.yunnan.jobmonitor.web.dto.ReportResponse;
import com.yunnan.jobmonitor.web.dto.ReportWriteDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReportController {

  private final ReportService reportService;

  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  @GetMapping("/me/reports")
  public List<ReportResponse> myReports() {
    return reportService.listMine().stream().map(WebMapper::toReport).toList();
  }

  @PostMapping("/me/reports")
  public ReportResponse saveDraft(@Valid @RequestBody ReportWriteDto dto) {
    return WebMapper.toReport(reportService.saveDraft(dto));
  }

  @PostMapping("/me/reports/{id}/submit")
  public ReportResponse submit(@PathVariable Long id) {
    return WebMapper.toReport(reportService.submit(id));
  }

  @GetMapping("/city/reports/pending")
  public List<ReportResponse> cityPending() {
    return reportService.pendingForCity().stream().map(WebMapper::toReport).toList();
  }

  @PostMapping("/city/reports/{id}/approve")
  public ReportResponse cityApprove(@PathVariable Long id) {
    return WebMapper.toReport(reportService.cityApprove(id));
  }

  @PostMapping("/city/reports/{id}/return")
  public ReportResponse cityReturn(@PathVariable Long id, @RequestBody(required = false) RemarkRequest remark) {
    return WebMapper.toReport(reportService.cityReturn(id, remark == null ? null : remark.remark()));
  }

  @GetMapping("/province/reports/pending")
  public List<ReportResponse> provincePending() {
    return reportService.pendingForProvince().stream().map(WebMapper::toReport).toList();
  }

  @GetMapping("/province/reports")
  public List<ReportResponse> provinceAll() {
    return reportService.allReports().stream().map(WebMapper::toReport).toList();
  }

  @PostMapping("/province/reports/{id}/approve")
  public ReportResponse provinceApprove(@PathVariable Long id) {
    return WebMapper.toReport(reportService.provinceApprove(id));
  }

  @PostMapping("/province/reports/{id}/return")
  public ReportResponse provinceReturn(
      @PathVariable Long id, @RequestBody(required = false) RemarkRequest remark) {
    return WebMapper.toReport(reportService.provinceReturn(id, remark == null ? null : remark.remark()));
  }

  @PostMapping("/province/reports/{id}/mark-national")
  public ReportResponse markNational(@PathVariable Long id) {
    return WebMapper.toReport(reportService.markNational(id));
  }

  @PostMapping("/province/reports/{id}/corrections")
  public void correct(@PathVariable Long id, @Valid @RequestBody CorrectionRequest req) {
    reportService.correct(id, req);
  }
}
