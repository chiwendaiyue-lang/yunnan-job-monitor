package com.yunnan.jobmonitor.service;

import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.domain.SysRole;
import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.EmploymentReportRepository;
import com.yunnan.jobmonitor.repo.SysRoleRepository;
import com.yunnan.jobmonitor.repo.SysUserRepository;
import com.yunnan.jobmonitor.web.dto.UserCreateDto;
import com.yunnan.jobmonitor.web.error.BusinessException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {

  private final SysUserRepository userRepository;
  private final SysRoleRepository roleRepository;
  private final EmploymentReportRepository reportRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentUserService currentUserService;

  public UserAdminService(
      SysUserRepository userRepository,
      SysRoleRepository roleRepository,
      EmploymentReportRepository reportRepository,
      PasswordEncoder passwordEncoder,
      CurrentUserService currentUserService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.reportRepository = reportRepository;
    this.passwordEncoder = passwordEncoder;
    this.currentUserService = currentUserService;
  }

  @Transactional(readOnly = true)
  public List<SysUser> listAll() {
    requireProvince();
    return userRepository.findAll();
  }

  @Transactional
  public SysUser create(UserCreateDto dto) {
    requireProvince();
    if (userRepository.findByUsername(dto.username()).isPresent()) {
      throw new BusinessException("用户名已存在");
    }
    SysRole role =
        roleRepository
            .findByCode(dto.roleCode())
            .orElseThrow(() -> new BusinessException("角色不存在"));
    SysUser u = new SysUser();
    u.setUsername(dto.username());
    u.setPasswordHash(passwordEncoder.encode(dto.password()));
    u.setDisplayName(dto.displayName());
    u.setRole(role);
    u.setRegionCode(dto.regionCode());
    u.setEnterpriseId(dto.enterpriseId());
    return userRepository.save(u);
  }

  @Transactional
  public void delete(Long id) {
    requireProvince();
    SysUser target = userRepository.findById(id).orElseThrow();
    if (target.getRole().getCode() == RoleCode.ENTERPRISE && target.getEnterpriseId() != null) {
      if (reportRepository.countByEnterprise_Id(target.getEnterpriseId()) > 0) {
        throw new BusinessException("该企业用户已有上报数据，不能删除");
      }
    }
    userRepository.delete(target);
  }

  private void requireProvince() {
    SysUser u = currentUserService.requireUser();
    if (u.getRole().getCode() != RoleCode.PROVINCE) {
      throw new BusinessException("仅省用户可管理账号");
    }
  }
}
