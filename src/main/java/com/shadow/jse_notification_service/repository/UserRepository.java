package com.shadow.jse_notification_service.repository;

import com.shadow.jse_notification_service.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
