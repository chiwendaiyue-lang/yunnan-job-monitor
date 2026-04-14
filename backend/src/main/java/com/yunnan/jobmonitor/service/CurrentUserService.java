package com.yunnan.jobmonitor.service;

import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.SysUserRepository;
import com.yunnan.jobmonitor.web.error.BusinessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

  private final SysUserRepository userRepository;

  public CurrentUserService(SysUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public SysUser requireUser() {
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository
        .findByUsername(name)
        .filter(SysUser::isActive)
        .orElseThrow(() -> new BusinessException("用户无效"));
  }
}
