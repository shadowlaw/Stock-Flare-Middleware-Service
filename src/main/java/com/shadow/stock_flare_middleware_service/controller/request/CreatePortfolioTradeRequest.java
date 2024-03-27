package com.shadow.stock_flare_middleware_service.controller.request;

import com.shadow.stock_flare_middleware_service.annotations.ValidEnumConstant;
import com.shadow.stock_flare_middleware_service.constants.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;

import java.math.BigDecimal;

import static com.shadow.stock_flare_middleware_service.constants.Validation.SYMBOL_ID_REGEX;

@Data
@RequiredArgsConstructor
public class CreatePortfolioTradeRequest {

    @Schema(description = "Ticker/Symbol ID for an investment instrument")
    @Pattern(regexp = SYMBOL_ID_REGEX, message = "Symbol id must be alphanumeric and 2-15 characters in length")
    @NotBlank(message = "This field is required")
    private String symbolId;

    @Schema(description = "Number of units bought/sold in the trade")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum value must be greater than 0 and can be fractional")
    @Digits(integer = 1000000, fraction = 2, message = "Number exceeds allowed value")
    @NotNull(message = "This field is required")
    private BigDecimal noOfUnits;

    @Schema(description = "Dollar value per unit of instrument")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum value must be greater than 0 and can be fractional")
    @Digits(integer = 1000000, fraction = 2, message = "Number exceeds allowed value")
    @NotNull(message = "This field is required")
    private BigDecimal amountPerUnit;

    @Schema(description = "Type of trade executed. Typically BUY/SELL")
    @ValidEnumConstant(enumClazz = TradeType.class)
    @NotBlank(message = "This field is required")
    private String type;

    @Schema(description = "Taxes paid on trade")
    @DecimalMin(value = "0.0", message = "Minimum value must be 0 or greater and can be fractional")
    @Digits(integer = 1000000, fraction = 2, message = "Number exceeds allowed value")
    private BigDecimal taxes;

    @Schema(description = "Broker fees paid on trade")
    @DecimalMin(value = "0.0", message = "Minimum value must be 0 or greater and can be fractional")
    @Digits(integer = 1000000, fraction = 2, message = "Number exceeds allowed value")
    private BigDecimal brokerFees;

    @Schema(description = "Any other fees paid on trade")
    @DecimalMin(value = "0.0", message = "Minimum value must be 0 or greater and can be fractional")
    @Digits(integer = 1000000, fraction = 2, message = "Number exceeds allowed value")
    private BigDecimal otherFees;

}
