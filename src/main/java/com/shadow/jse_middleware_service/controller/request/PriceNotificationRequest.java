package com.shadow.jse_middleware_service.controller.request;

import com.shadow.jse_middleware_service.annotations.ValidEnumConstant;
import com.shadow.jse_middleware_service.constants.PriceTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Schema(name = "Price Notification Request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceNotificationRequest {

    @Schema(description = "Type of price notification to subscribe to", required = true)
    @ValidEnumConstant(enumClazz = PriceTargetType.class)
    String notificationType;

    @Schema(description = "Medium Id to send  notification to", required = true)
    @NotBlank
    @Pattern(regexp = "^[0-9]{9}$", message = "Invalid medium id format")
    private String mediumId;
}
