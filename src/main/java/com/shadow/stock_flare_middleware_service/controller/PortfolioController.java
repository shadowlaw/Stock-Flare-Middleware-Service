package com.shadow.stock_flare_middleware_service.controller;

import com.shadow.stock_flare_middleware_service.constants.PortfolioType;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioRequest;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioTradeRequest;
import com.shadow.stock_flare_middleware_service.controller.response.CreatePortfolioResponse;
import com.shadow.stock_flare_middleware_service.controller.response.CreatePortfolioTradeResponse;
import com.shadow.stock_flare_middleware_service.controller.response.ErrorResponse;

import com.shadow.stock_flare_middleware_service.repository.entity.DividendPayment;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.shadow.stock_flare_middleware_service.constants.LoggingConstants.REQUEST_ID;
import static com.shadow.stock_flare_middleware_service.constants.Validation.*;

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
            @Pattern(regexp = USER_ID_REGEX, message = USER_ID_REGEX_MESSAGE)
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
            @ApiResponse(responseCode = "201", description = "Trades Created", content = @Content(schema = @Schema(implementation = CreatePortfolioTradeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource Not Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "{portfolio_id}/trade", method = RequestMethod.POST)
    public ResponseEntity<?> createTrade(
            @PathVariable(name = "portfolio_id")
            @Pattern(regexp = PORTFOLIO_ID_REGEX, message = PORTFOLIO_ID_REGEX_MESSAGE)
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

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatePortfolioTradeResponse(HttpStatus.CREATED.value(), portfolioTrades));
    }

    @Operation(summary = "Retrieve dividend payment by portfolio", description = "Retrieve dividend payments by portfolio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request processed. A list of payment data is returned if found, otherwise, an empty list", content = @Content(array = @ArraySchema(arraySchema = @Schema(implementation = DividendPayment.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource Not Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "{portfolio_id}/dividend", method = RequestMethod.GET)
    public ResponseEntity<?> getDividendPayments(
            @PathVariable(value = "portfolio_id")
            @Pattern(regexp = PORTFOLIO_ID_REGEX, message = PORTFOLIO_ID_REGEX_MESSAGE)
            @Schema(description = "Unique ID assigned to a portfolio")
            String portfolioId,
            @RequestParam(value = "symbol", required = false)
            @Pattern(regexp = SYMBOL_ID_REGEX, message = SYMBOL_ID_REGEX_MESSAGE)
            @Schema(description = "Symbol ID to filter results by")
            String symbolId,
            @RequestParam(value = "start-date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Schema(description = "Start date for payment date range. Use date format yyyy-MM-dd", example = "yyyy-MM-dd")
            LocalDate paymentStartDate,
            @RequestParam(value = "end-date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Schema(description = "End date for payment date range. Use date format yyyy-MM-dd", example = "yyyy-MM-dd")
            LocalDate paymentEndDate
    ){
        MDC.put(REQUEST_ID, "GET_DIVIDEND");
        MDC.put("PORTFOLIO_ID", portfolioId);
        MDC.put("SYMBOL", symbolId);
        MDC.put("START_DATE", paymentStartDate.toString());
        MDC.put("END_DATE", paymentEndDate.toString());

        List<DividendPayment> dividendPayments = portfolioManagementService.getDividends(portfolioId, symbolId, paymentStartDate, paymentEndDate);

        HttpStatus status = null;
        log.debug("portfolio: {} | return size: {}", portfolioId, dividendPayments.size());
        status = dividendPayments.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        log.debug("setting return status to [{}]", status);
        log.info("Dividend payments retrieval complete for portfolio [{}]", portfolioId);

        return ResponseEntity.status(status).body(dividendPayments);
    }
}
