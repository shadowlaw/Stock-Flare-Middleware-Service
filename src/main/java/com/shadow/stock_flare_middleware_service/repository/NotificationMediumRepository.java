package com.shadow.stock_flare_middleware_service.repository;

import com.shadow.stock_flare_middleware_service.repository.entity.NotificationMedium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationMediumRepository extends JpaRepository<NotificationMedium, String> {
    Optional<NotificationMedium> findByUserIdAndMediumId(String user_id, String medium_id);
}
