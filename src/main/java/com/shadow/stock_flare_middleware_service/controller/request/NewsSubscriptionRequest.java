package com.shadow.stock_flare_middleware_service.controller.request;

import com.shadow.stock_flare_middleware_service.annotations.ValidEnumConstant;
import com.shadow.stock_flare_middleware_service.constants.NewsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="News Subscription Request", description = "Request object for news subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriptionRequest {

    @Schema(description = "News type to subscribe to", required = true)
    @ValidEnumConstant(enumClazz = NewsType.class)
    private String newsType;

}
