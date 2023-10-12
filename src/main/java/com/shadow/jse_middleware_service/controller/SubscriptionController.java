package com.shadow.jse_middleware_service.controller;

import com.shadow.jse_middleware_service.annotations.ValidEnumConstant;
import com.shadow.jse_middleware_service.constants.NewsType;
import com.shadow.jse_middleware_service.constants.PriceTargetType;
import com.shadow.jse_middleware_service.controller.request.NewsSubscriptionRequest;
import com.shadow.jse_middleware_service.controller.request.PriceNotificationRequest;
import com.shadow.jse_middleware_service.controller.response.ErrorResponse;
import com.shadow.jse_middleware_service.controller.response.Response;
import com.shadow.jse_middleware_service.controller.response.PagedDataResponse;
import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.service.SubscriptionManagementService;
import com.shadow.jse_middleware_service.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import static com.shadow.jse_middleware_service.constants.LoggingConstants.*;
import static com.shadow.jse_middleware_service.constants.Validation.MEDIUM_ID_REGEX;
import static com.shadow.jse_middleware_service.constants.Validation.SYMBOL_ID_REGEX;

@Tag(name = "Notification subscription endpoints",  description = "Mange user subscriptions to notifications")
@Validated
@Slf4j
@RestController
@RequestMapping(value = "api/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class SubscriptionController {

    @Autowired
    SubscriptionManagementService subscriptionManagementService;

    @Value("${app.api.subscription.page.default_size}")
    private Integer defaultPageSize;

    @Value("${app.api.subscription.page.max_page_size}")
    private Integer maxPageSize;

    @Operation(summary = "Retrieve Notification subscriptions",description = "Retrieve notification subscriptions by medium", tags = "Notification subscription endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification Subscriptions found", content = @Content(schema = @Schema(implementation = PagedDataResponse.class), mediaType =  MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "No subscription data found.", content = @Content(schema = @Schema(implementation = PagedDataResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("{mediumId}")
    public ResponseEntity<?> getSubscriptions(
            @PathVariable
            @Pattern(regexp = MEDIUM_ID_REGEX, message = "Invalid medium id format")
            String mediumId,
            @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "size", defaultValue = "${app.api.subscription.page.default_size}") Integer pageSize
    ){

        MDC.put(REQUEST_ID, "GET_SUBSCRIPTIONS");

        log.debug(String.format("original page number: %s | original page size : %s", pageNumber, pageSize));

        PageRequest pageRequest = PageUtil.getPageRequest(pageNumber, pageSize, defaultPageSize, maxPageSize);

        MDC.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
        MDC.put(PAGE_SIZE, String.valueOf(pageRequest.getOffset()));

        Page<NotificationSubscription> subscriptionPage = subscriptionManagementService.getNotificationSubscription(mediumId, pageRequest);

        HttpStatus responseStatus = subscriptionPage.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        log.debug(String.format("page content size: %s", subscriptionPage.getNumberOfElements()));
        log.info("Request processed");

        return ResponseEntity.status(responseStatus).body(new PagedDataResponse(responseStatus.value(), subscriptionPage));
    }

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
            @Pattern(regexp = SYMBOL_ID_REGEX,
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
            @Pattern(regexp = SYMBOL_ID_REGEX, message = "symbol id must be alphanumeric and 3-9 characters in length")
            String symbol,
            @PathVariable("notification_type")
            @ValidEnumConstant(enumClazz = NewsType.class)
            String newsType,
            @PathVariable("medium_id")
            @Pattern(regexp = MEDIUM_ID_REGEX, message = "Invalid medium id format")
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
            @Pattern(regexp = SYMBOL_ID_REGEX,
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
            @Pattern(regexp = SYMBOL_ID_REGEX,
                    message = "symbol id must be alphanumeric and 3-9 characters in length"
            )
            @PathVariable("symbol") String symbolId,
            @ValidEnumConstant(enumClazz = PriceTargetType.class)
            @PathVariable("notification_type") String notificationType,
            @Pattern(regexp = MEDIUM_ID_REGEX, message = "Invalid medium id format")
            @PathVariable("medium_id") String mediumId
    ){
        MDC.put(REQUEST_ID, "CREATE_PRICE_NOTIFICATION");
        MDC.put(USER_ID, userId);
        MDC.put(SYMBOL, symbolId);
        MDC.put(MEDIUM_ID, mediumId);

        subscriptionManagementService.deletePriceNotification(userId, symbolId, notificationType, mediumId);

        Response response = new Response(HttpStatus.NO_CONTENT.value());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

}
