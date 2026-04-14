package com.yunnan.jobmonitor.repo;

import com.yunnan.jobmonitor.domain.SysUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {
  Optional<SysUser> findByUsername(String username);

  @Query("SELECT COUNT(r) FROM EmploymentReport r WHERE r.enterprise.id IN "
      + "(SELECT u.enterpriseId FROM SysUser u WHERE u.id = :userId AND u.enterpriseId IS NOT NULL)")
  long countReportsByEnterpriseUser(@Param("userId") Long userId);
}
