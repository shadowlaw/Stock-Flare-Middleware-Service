package com.shadow.stock_flare_middleware_service.controller.request;

import com.shadow.stock_flare_middleware_service.annotations.ValidEnumConstant;
import com.shadow.stock_flare_middleware_service.constants.PriceTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Price Notification Request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceNotificationRequest {

    @Schema(description = "Type of price notification to subscribe to", required = true)
    @ValidEnumConstant(enumClazz = PriceTargetType.class)
    String notificationType;
}
