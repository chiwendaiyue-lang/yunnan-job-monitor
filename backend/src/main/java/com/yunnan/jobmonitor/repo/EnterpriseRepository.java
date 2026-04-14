package com.yunnan.jobmonitor.repo;

import com.yunnan.jobmonitor.domain.Enterprise;
import com.yunnan.jobmonitor.domain.FilingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
  List<Enterprise> findByFilingStatus(FilingStatus status);

  List<Enterprise> findByRegionCity(String regionCity);
}
