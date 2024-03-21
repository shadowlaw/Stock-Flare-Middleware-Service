package com.shadow.stock_flare_middleware_service.repository;

import com.shadow.stock_flare_middleware_service.repository.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, String> {
}
