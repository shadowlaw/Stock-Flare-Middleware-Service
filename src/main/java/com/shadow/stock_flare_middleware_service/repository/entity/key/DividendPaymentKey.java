package com.shadow.stock_flare_middleware_service.repository.entity.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DividendPaymentKey implements Serializable {

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "record_date")
    private String recordDate;
}
