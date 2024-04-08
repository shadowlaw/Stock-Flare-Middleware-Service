package com.shadow.stock_flare_middleware_service.repository.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.shadow.stock_flare_middleware_service.repository.entity.key.DividendPaymentKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@NamedStoredProcedureQuery(name = "DividendPayment.getPayments", procedureName = "get_dividend_payments", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "portfolio_id", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "symbols", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "start_pmt_date", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "end_pmt_date", type = String.class)
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DividendPayment {


    @EmbeddedId
    @JsonUnwrapped
    DividendPaymentKey key;

    @Column(name = "ex_date")
    private String exDate;

    @Column(name = "payment_date")
    private String paymentDate;

    @Column(name = "unit_amount")
    private BigDecimal unitAmount;

    @Column(name = "total_units_bought")
    private BigDecimal totalUnitsBought;

    @Column(name = "total_units_sold")
    private BigDecimal totalUnitsSold;

    @Column(name = "total_active_units")
    private BigDecimal totalActiveUnits;

    @Column(name = "tax_paid")
    private BigDecimal taxPaid;

    @Column(name = "gross_payment")
    private BigDecimal grossPayment;

    @Column(name = "net_payment")
    private BigDecimal netPayment;
}
