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

import static com.shadow.jse_middleware_service.constants.Validation.MEDIUM_ID_REGEX;

@Schema(name="News Subscription Request", description = "Request object for news subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriptionRequest {

    @Schema(description = "News type to subscribe to", required = true)
    @ValidEnumConstant(enumClazz = NewsType.class)
    private String newsType;

}
