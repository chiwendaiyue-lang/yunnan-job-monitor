package com.yunnan.jobmonitor.repo;

import com.yunnan.jobmonitor.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  @Query("SELECT n FROM Notification n JOIN FETCH n.publisher ORDER BY n.createdAt DESC")
  List<Notification> findAllWithPublisher();
}
