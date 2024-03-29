package com.shadow.stock_flare_middleware_service.controller;

import com.shadow.stock_flare_middleware_service.constants.PortfolioType;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioRequest;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioTradeRequest;
import com.shadow.stock_flare_middleware_service.controller.response.CreatePortfolioResponse;
import com.shadow.stock_flare_middleware_service.controller.response.ErrorResponse;

import com.shadow.stock_flare_middleware_service.repository.entity.Portfolio;
import com.shadow.stock_flare_middleware_service.repository.entity.PortfolioTrade;
import com.shadow.stock_flare_middleware_service.service.PortfolioManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.shadow.stock_flare_middleware_service.constants.LoggingConstants.REQUEST_ID;
import static com.shadow.stock_flare_middleware_service.constants.Validation.PORTFOLIO_ID_REGEX;
import static com.shadow.stock_flare_middleware_service.constants.Validation.USER_ID_REGEX;

@Tag(name = "Portfolio Management", description = "Manage user portfolio details")
@Validated
@Slf4j
@RestController
@RequestMapping( value = "api/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
public class PortfolioController {

    @Autowired
    PortfolioManagementService portfolioManagementService;

    @Operation(summary = "Create user portfolio", description = "Allows for the creations of a portfolio for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Portfolio was created", content = @Content(schema = @Schema(implementation = CreatePortfolioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request details", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unable to find user for portfolio association", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content())
    })
    @RequestMapping( value = "{userId}", method = RequestMethod.POST)
    public ResponseEntity<?> createPortfolio(
            @PathVariable(name = "userId")
            @Pattern(regexp = USER_ID_REGEX, message = "must be numeric")
            String userId,
            @RequestBody @Valid CreatePortfolioRequest requestBody
            ){

        MDC.put(REQUEST_ID, "CREATE_PORTFOLIO");

        Optional<Portfolio> portfolioOpt = portfolioManagementService.createPortfolio(userId,
                requestBody.getNickname(), requestBody.getIdentifier(), PortfolioType.valueOf(requestBody.getType()).name());

        if (portfolioOpt.isEmpty()) {
            log.error("Unable to create portfolio");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatePortfolioResponse(
                HttpStatus.CREATED.value(),
                portfolioOpt.get().getId(),
                portfolioOpt.get().getNickname(),
                portfolioOpt.get().getExternalId(),
                portfolioOpt.get().getType()
        ));
    }

    @Operation(summary = "Create trades for a portfolio", description = "Creates a buy or sell trade for a portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request Processed, no trades created", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "201", description = "Trades Created", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PortfolioTrade.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource Not Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "{portfolio_id}/trade", method = RequestMethod.POST)
    public ResponseEntity<?> createTrade(
            @PathVariable(name = "portfolio_id")
            @Pattern(regexp = PORTFOLIO_ID_REGEX, message = "Invalid portfolio ID")
            @Schema(description = "Unique ID assigned to a portfolio")
            String portfolioId,
            @RequestBody @Valid
            @NotEmpty(message = "At least 1 tade is required")
            List<CreatePortfolioTradeRequest> portfolioTradeRequest
    ) {

        MDC.put(REQUEST_ID, "CREATE_TRADE");

        List<PortfolioTrade> portfolioTrades =  portfolioTradeRequest.stream().map(tradeRequest ->
                portfolioManagementService.createTrade(portfolioId, tradeRequest.getSymbolId(), tradeRequest.getNoOfUnits(),
                        tradeRequest.getAmountPerUnit(), tradeRequest.getType(), tradeRequest.getTransactionDate(), tradeRequest.getTaxes(), tradeRequest.getBrokerFees(),
                        tradeRequest.getOtherFees()))
                .filter(Objects::nonNull).collect(Collectors.toList());

        if (portfolioTrades.isEmpty()) {
            log.error("Trade request creation failed. No trades created.");
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioTrades);
    }
}
