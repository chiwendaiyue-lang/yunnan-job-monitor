package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.AuditLog;
import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.repo.AuditLogRepository;
import com.yunnan.jobmonitor.service.CurrentUserService;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/province/audit-logs")
public class AuditLogController {

  private final AuditLogRepository auditLogRepository;
  private final CurrentUserService currentUserService;

  public AuditLogController(AuditLogRepository auditLogRepository, CurrentUserService currentUserService) {
    this.auditLogRepository = auditLogRepository;
    this.currentUserService = currentUserService;
  }

  @GetMapping
  public List<AuditLog> list() {
    if (currentUserService.requireUser().getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可查看审计日志");
    }
    return auditLogRepository.findAll().stream()
        .sorted(Comparator.comparing(AuditLog::getCreatedAt).reversed())
        .toList();
  }
}
