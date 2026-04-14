package com.yunnan.jobmonitor.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnan.jobmonitor.domain.Enterprise;
import com.yunnan.jobmonitor.domain.FilingStatus;
import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.service.CurrentUserService;
import com.yunnan.jobmonitor.service.EnterpriseService;
import com.yunnan.jobmonitor.web.dto.EnterpriseResponse;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/province/export")
public class ExportController {

  private final EnterpriseService enterpriseService;
  private final CurrentUserService currentUserService;
  private final ObjectMapper objectMapper;

  public ExportController(
      EnterpriseService enterpriseService,
      CurrentUserService currentUserService,
      ObjectMapper objectMapper) {
    this.enterpriseService = enterpriseService;
    this.currentUserService = currentUserService;
    this.objectMapper = objectMapper;
  }

  @GetMapping("/enterprises")
  public ResponseEntity<byte[]> exportEnterprises(@RequestParam(defaultValue = "csv") String format)
      throws JsonProcessingException {
    if (currentUserService.requireUser().getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可导出");
    }
    List<Enterprise> rows = enterpriseService.listForProvince(null, FilingStatus.APPROVED);
    List<EnterpriseResponse> dtos = rows.stream().map(WebMapper::toEnterprise).toList();
    byte[] body;
    MediaType mediaType;
    String filename;
    if ("json".equalsIgnoreCase(format)) {
      body = objectMapper.writeValueAsBytes(dtos);
      mediaType = MediaType.APPLICATION_JSON;
      filename = "enterprises.json";
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append('\ufeff');
      sb.append(
          "id,orgCode,name,regionCity,regionCounty,filingStatus,phone,email\n");
      for (EnterpriseResponse e : dtos) {
        sb.append(e.id())
            .append(',')
            .append(csv(e.orgCode()))
            .append(',')
            .append(csv(e.name()))
            .append(',')
            .append(csv(e.regionCity()))
            .append(',')
            .append(csv(e.regionCounty()))
            .append(',')
            .append(e.filingStatus())
            .append(',')
            .append(csv(e.phone()))
            .append(',')
            .append(csv(e.email() == null ? "" : e.email()))
            .append('\n');
      }
      body = sb.toString().getBytes(StandardCharsets.UTF_8);
      mediaType = new MediaType("text", "csv", StandardCharsets.UTF_8);
      filename = "enterprises.csv";
    }
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
        .contentType(mediaType)
        .body(body);
  }

  private static String csv(String v) {
    if (v == null) {
      return "";
    }
    String s = v.replace("\"", "\"\"");
    if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
      return "\"" + s + "\"";
    }
    return s;
  }
}
