package com.shadow.stock_flare_middleware_service.repository.entity;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@RequiredArgsConstructor
@Data
@Entity(name = "portfolio_trade")
public class PortfolioTrade {

    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    @Column(name = "portfolio_id")
    private String portfolioId;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "no_of_units")
    private BigDecimal noOfUnits;

    @Column(name = "value_per_unit")
    private BigDecimal amountPerUnit;

    @Column(name = "trade_type")
    private String type;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "tax_paid")
    private BigDecimal taxes;

    @Column(name = "broker_fees")
    private BigDecimal brokerFees;

    @Column(name = "other_fees")
    private BigDecimal otherFees;

    public void setTaxes(BigDecimal taxes) {
        this.taxes =  taxes != null ? taxes : new BigDecimal("0");
    }

    public void setBrokerFees(BigDecimal brokerFees) {
        this.brokerFees = brokerFees != null ? brokerFees : new BigDecimal("0");
    }

    public void setOtherFees(BigDecimal otherFees) {
        this.otherFees = otherFees != null ? otherFees : new BigDecimal("0");
    }
}
