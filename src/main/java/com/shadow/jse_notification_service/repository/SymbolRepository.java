package com.shadow.jse_notification_service.repository;

import com.shadow.jse_notification_service.repository.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, String> {
}
