package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.Notification;
import com.yunnan.jobmonitor.service.NotificationService;
import com.yunnan.jobmonitor.web.dto.NotificationWriteDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/notifications")
  public List<Notification> list() {
    return notificationService.listAll();
  }

  @PostMapping("/province/notifications")
  public Notification createProvince(@Valid @RequestBody NotificationWriteDto dto) {
    return notificationService.create(dto);
  }

  @PostMapping("/city/notifications")
  public Notification createCity(@Valid @RequestBody NotificationWriteDto dto) {
    return notificationService.create(dto);
  }

  @PutMapping("/province/notifications/{id}")
  public Notification update(@PathVariable Long id, @Valid @RequestBody NotificationWriteDto dto) {
    return notificationService.update(id, dto);
  }

  @DeleteMapping("/province/notifications/{id}")
  public void delete(@PathVariable Long id) {
    notificationService.delete(id);
  }
}
