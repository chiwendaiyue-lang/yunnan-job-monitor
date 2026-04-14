package com.yunnan.jobmonitor.repo;

import com.yunnan.jobmonitor.domain.RoleCode;
import com.yunnan.jobmonitor.domain.SysRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
  Optional<SysRole> findByCode(RoleCode code);
}
