package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.service.CurrentUserService;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/province/system")
public class SystemMetricsController {

  private final CurrentUserService currentUserService;

  public SystemMetricsController(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  @GetMapping("/metrics")
  public Map<String, Object> metrics() {
    if (currentUserService.requireUser().getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可查看系统监控");
    }
    MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("heapUsedBytes", heap.getUsed());
    map.put("heapMaxBytes", heap.getMax());
    map.put("processors", Runtime.getRuntime().availableProcessors());
    map.put("systemLoadAverage", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
    File root = new File(".");
    map.put("diskFreeBytes", root.getFreeSpace());
    map.put("diskTotalBytes", root.getTotalSpace());
    map.put("appUptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
    return map;
  }
}
