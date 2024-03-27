package com.shadow.stock_flare_middleware_service.repository;

import com.shadow.stock_flare_middleware_service.repository.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, String> {

}
