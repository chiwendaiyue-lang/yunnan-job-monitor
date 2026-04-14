package com.yunnan.jobmonitor.repo;

import com.yunnan.jobmonitor.domain.DataCorrection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataCorrectionRepository extends JpaRepository<DataCorrection, Long> {
  List<DataCorrection> findByReport_IdOrderByCreatedAtDesc(Long reportId);
}
