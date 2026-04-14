package com.yunnan.jobmonitor.service;

import com.yunnan.jobmonitor.domain.Notification;
import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.NotificationRepository;
import com.yunnan.jobmonitor.web.dto.NotificationWriteDto;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final CurrentUserService currentUserService;

  public NotificationService(
      NotificationRepository notificationRepository, CurrentUserService currentUserService) {
    this.notificationRepository = notificationRepository;
    this.currentUserService = currentUserService;
  }

  @Transactional(readOnly = true)
  public List<Notification> listAll() {
    return notificationRepository.findAllWithPublisher();
  }

  @Transactional
  public Notification create(NotificationWriteDto dto) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE && u.getRole().getCode() != RoleCode.CITY) {
      throw new BusinessException("仅省或市用户可发布通知");
    }
    Notification n = new Notification();
    n.setTitle(dto.title());
    n.setContent(dto.content());
    n.setPublisher(u);
    n.setPublisherUnit(u.getDisplayName());
    return notificationRepository.save(n);
  }

  @Transactional
  public Notification update(Long id, NotificationWriteDto dto) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可修改通知");
    }
    Notification n = notificationRepository.findById(id).orElseThrow();
    n.setTitle(dto.title());
    n.setContent(dto.content());
    return notificationRepository.save(n);
  }

  @Transactional
  public void delete(Long id) {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可删除通知");
    }
    notificationRepository.deleteById(id);
  }
}
