package com.yunnan.jobmonitor.repo;

import com.yunnan.jobmonitor.domain.EmploymentReport;
import com.yunnan.jobmonitor.domain.ReportStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmploymentReportRepository extends JpaRepository<EmploymentReport, Long> {
  Optional<EmploymentReport> findByEnterpriseIdAndSurveyPeriodId(Long enterpriseId, Long surveyPeriodId);

  @Query(
      "SELECT r FROM EmploymentReport r JOIN FETCH r.enterprise e JOIN FETCH r.surveyPeriod p "
          + "WHERE e.id = :enterpriseId ORDER BY p.id DESC")
  List<EmploymentReport> listByEnterpriseWithDetails(@Param("enterpriseId") Long enterpriseId);

  @Query(
      "SELECT r FROM EmploymentReport r JOIN FETCH r.enterprise e JOIN FETCH r.surveyPeriod p "
          + "WHERE e.regionCity = :city AND r.status IN :statuses")
  List<EmploymentReport> findPendingForCity(
      @Param("city") String city, @Param("statuses") Collection<ReportStatus> statuses);

  long countByEnterprise_Id(Long enterpriseId);

  @Query("SELECT r FROM EmploymentReport r JOIN FETCH r.enterprise e JOIN FETCH r.surveyPeriod p")
  List<EmploymentReport> findAllWithDetails();
}
