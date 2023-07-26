package com.shadow.jse_middleware_service.repository;

import com.shadow.jse_middleware_service.repository.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, String> {
}
