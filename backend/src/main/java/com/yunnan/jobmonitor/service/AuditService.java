package com.yunnan.jobmonitor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnan.jobmonitor.domain.AuditLog;
import com.yunnan.jobmonitor.repo.AuditLogRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

  private final AuditLogRepository auditLogRepository;
  private final ObjectMapper objectMapper;

  public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
    this.auditLogRepository = auditLogRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public void log(Long userId, String action, String entityType, Long entityId, Map<String, Object> detail) {
    AuditLog row = new AuditLog();
    row.setUserId(userId);
    row.setAction(action);
    row.setEntityType(entityType);
    row.setEntityId(entityId);
    if (detail != null && !detail.isEmpty()) {
      try {
        row.setDetailJson(objectMapper.writeValueAsString(detail));
      } catch (JsonProcessingException e) {
        row.setDetailJson("{\"error\":\"json\"}");
      }
    }
    auditLogRepository.save(row);
  }
}
