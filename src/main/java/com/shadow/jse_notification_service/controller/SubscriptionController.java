package com.shadow.jse_notification_service.controller;

import com.shadow.jse_notification_service.controller.request.NewsSubscriptionRequest;
import com.shadow.jse_notification_service.controller.response.ErrorResponse;
import com.shadow.jse_notification_service.controller.response.ExampleResponse;
import com.shadow.jse_notification_service.service.SubscriptionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import static com.shadow.jse_notification_service.constants.LoggingConstants.REQUEST_ID;

@Tag(name = "Notification subscription endpoints",  description = "Mange user subscriptions to notifications")
@Validated
@RestController
@RequestMapping("api/subscriptions")
public class SubscriptionController {

    @Autowired
    SubscriptionManagementService subscriptionManagementService;

    @Operation(summary = "Create news subscription",description = "Create subscription to symbol news for user", tags = "Notification subscription endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification Subscription Created", content = @Content(schema = @Schema(implementation = ExampleResponse.class), mediaType =  MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("users/{user_id}/symbols/{symbol}/news")
    public ResponseEntity<?> newsSubscribe (
            @PathVariable("user_id") String user_id,
            @PathVariable("symbol")
            @Pattern(regexp = "^(?=.*[A-Z])[\\w.]{3,9}$",
                    message = "symbol id must be alphanumeric and 3-9 characters in length"
            ) String symbol,
            @RequestBody @Valid NewsSubscriptionRequest subscriptionRequest) {

        MDC.put(REQUEST_ID, "CREATE_NEWS_NOTIFICATION");

        subscriptionManagementService.createNewsNotification(user_id, symbol, subscriptionRequest.getNewsType(),
                subscriptionRequest.getMediumType(), subscriptionRequest.getMediumId());


        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

// [DELETE] api/<symbol>/news/subscription

}
