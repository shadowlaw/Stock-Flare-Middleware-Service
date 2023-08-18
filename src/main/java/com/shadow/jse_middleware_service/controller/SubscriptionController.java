package com.shadow.jse_middleware_service.controller;

import com.shadow.jse_middleware_service.annotations.ValidEnumConstant;
import com.shadow.jse_middleware_service.constants.NewsType;
import com.shadow.jse_middleware_service.constants.PriceTargetType;
import com.shadow.jse_middleware_service.controller.request.NewsSubscriptionRequest;
import com.shadow.jse_middleware_service.controller.request.PriceNotificationRequest;
import com.shadow.jse_middleware_service.controller.response.ErrorResponse;
import com.shadow.jse_middleware_service.controller.response.Response;
import com.shadow.jse_middleware_service.service.SubscriptionManagementService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import static com.shadow.jse_middleware_service.constants.LoggingConstants.*;

@Tag(name = "Notification subscription endpoints",  description = "Mange user subscriptions to notifications")
@Validated
@RestController
@RequestMapping("api/subscriptions")
public class SubscriptionController {

    @Autowired
    SubscriptionManagementService subscriptionManagementService;

    @Operation(summary = "Create news subscription",description = "Create subscription to symbol news for user", tags = "Notification subscription endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification Subscription Created", content = @Content(schema = @Schema(implementation = Response.class), mediaType =  MediaType.APPLICATION_JSON_VALUE)),
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

        Response response = new Response(HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete news subscription",description = "Unsubscribes a user from symbol news", tags = "Notification subscription endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification Subscription deleted", content = @Content(schema = @Schema(implementation = Response.class), mediaType =  MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @DeleteMapping("users/{user_id}/symbols/{symbol_id}/news/{notification_type}/{medium_id}")
    public ResponseEntity<?> deleteNewsSubscription (
            @PathVariable("user_id") String userId,
            @PathVariable("symbol_id")
            @Pattern(regexp = "^(?=.*[A-Z])[\\w.]{3,9}$", message = "symbol id must be alphanumeric and 3-9 characters in length")
            String symbol,
            @PathVariable("notification_type")
            @ValidEnumConstant(enumClazz = NewsType.class)
            String newsType,
            @PathVariable("medium_id")
            @Pattern(regexp = "^[0-9]{9}$", message = "Invalid medium id format")
            String mediumId
            ) {

        MDC.put(REQUEST_ID, "DELETE_NEWS_NOTIFICATION");

        subscriptionManagementService.deleteNewsNotification(userId, symbol, newsType, mediumId);

        Response response = new Response(HttpStatus.NO_CONTENT.value());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @Operation(summary = "Create price notification update subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Price Notification Updated created", content = @Content(schema=@Schema(implementation = Response.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("users/{user_id}/symbols/{symbol}/price")
    public ResponseEntity<?> createPriceNotificationSubscription(
            @PathVariable("user_id") String userId,
            @PathVariable("symbol")
            @Pattern(regexp = "^(?=.*[A-Z])[\\w.]{3,9}$",
                    message = "symbol id must be alphanumeric and 3-9 characters in length"
            ) String symbolId,
            @RequestBody @Valid PriceNotificationRequest priceNotificationRequest
    ) {
        MDC.put(REQUEST_ID, "CREATE_PRICE_NOTIFICATION");
        MDC.put(USER_ID, userId);
        MDC.put(SYMBOL, symbolId);
        MDC.put(MEDIUM_ID, priceNotificationRequest.getMediumId());

        subscriptionManagementService.createPriceNotification(userId, symbolId, priceNotificationRequest.getNotificationType(), priceNotificationRequest.getMediumId());

        Response response = new Response(HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Delete price notification update subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete price notification subscription", content = @Content(schema = @Schema(implementation = Response.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @DeleteMapping("users/{user_id}/symbols/{symbol}/price/{notification_type}/{medium_id}")
    public ResponseEntity<?> deletePriceNotificationSubscription(
            @PathVariable("user_id") String userId,
            @Pattern(regexp = "^(?=.*[A-Z])[\\w.]{3,9}$",
                    message = "symbol id must be alphanumeric and 3-9 characters in length"
            )
            @PathVariable("symbol") String symbolId,
            @ValidEnumConstant(enumClazz = PriceTargetType.class)
            @PathVariable("notification_type") String notificationType,
            @Pattern(regexp = "^[0-9]{9}$", message = "Invalid medium id format")
            @PathVariable("medium_id") String mediumId
    ){
        MDC.put(REQUEST_ID, "CREATE_PRICE_NOTIFICATION");
        MDC.put(USER_ID, userId);
        MDC.put(SYMBOL, symbolId);
        MDC.put(MEDIUM_ID, mediumId);

        subscriptionManagementService.deletePriceNotification(userId, symbolId, notificationType, mediumId);

        Response response = new Response(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
