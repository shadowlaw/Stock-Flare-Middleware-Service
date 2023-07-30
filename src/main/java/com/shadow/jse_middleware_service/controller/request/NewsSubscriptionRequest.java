package com.shadow.jse_middleware_service.controller.request;

import com.shadow.jse_middleware_service.annotations.ValidEnumConstant;
import com.shadow.jse_middleware_service.constants.NewsType;
import com.shadow.jse_middleware_service.constants.NotificationMediumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Schema(name="News Subscription Request", description = "Request object for news subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriptionRequest {

    @Schema(description = "News type to subscribe to", required = true)
    @ValidEnumConstant(enumClazz = NewsType.class)
    private String newsType;

    @Schema(description = "Medium type of medium id", required = true)
    @ValidEnumConstant(enumClazz = NotificationMediumType.class)
    private String mediumType;

    @Schema(description = "Medium Id to send  notification to", required = true)
    @NotBlank
    @Pattern(regexp = "^[0-9]{9}$", message = "Invalid medium id format")
    private String mediumId;
}
