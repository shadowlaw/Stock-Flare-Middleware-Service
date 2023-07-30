package com.shadow.jse_middleware_service.repository;

import com.shadow.jse_middleware_service.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
