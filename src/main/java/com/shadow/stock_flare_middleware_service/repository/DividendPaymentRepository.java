package com.shadow.stock_flare_middleware_service.repository;

import com.shadow.stock_flare_middleware_service.repository.entity.DividendPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DividendPaymentRepository extends JpaRepository<DividendPayment, String> {

    @Procedure(procedureName = "get_dividend_payments")
    List<DividendPayment> getDividendPayments(
            @Param("portfolio_id") String portfolioId,
            @Param("symbols") String symbols,
            @Param("start_pmt_date") String paymentStartDate,
            @Param("end_pmt_date") String paymentEndDate
            );
}
