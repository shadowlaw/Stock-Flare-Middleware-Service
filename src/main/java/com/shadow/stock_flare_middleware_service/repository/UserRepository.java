package com.shadow.stock_flare_middleware_service.repository;

import com.shadow.stock_flare_middleware_service.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
