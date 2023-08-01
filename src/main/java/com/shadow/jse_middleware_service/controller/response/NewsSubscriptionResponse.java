package com.shadow.jse_middleware_service.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="News Subscription Response", description = "Response object for successful news subscriptions request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriptionResponse {
    private String subscriptionId;
}
