package com.shadow.jse_middleware_service.controller;

import com.shadow.jse_middleware_service.controller.response.ErrorResponse;
import com.shadow.jse_middleware_service.controller.response.PagedDataResponse;
import com.shadow.jse_middleware_service.repository.entity.Symbol;
import com.shadow.jse_middleware_service.service.SymbolService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.shadow.jse_middleware_service.constants.LoggingConstants.PAGE_NUMBER;
import static com.shadow.jse_middleware_service.constants.LoggingConstants.PAGE_SIZE;
import static com.shadow.jse_middleware_service.constants.LoggingConstants.REQUEST_ID;

@Tag(name = "Symbol data endpoints",  description = "Provides symbol data details")
@Validated
@RestController
@RequestMapping(value = "api/symbol", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class SymbolController {

    @Value("${app.api.symbol.page.default_size}")
    private Integer defaultPageSize;

    @Value("${app.api.symbol.page.max_page_size}")
    private Integer maxPageSize;

    @Autowired
    private SymbolService symbolService;

    @Operation(summary = "Retrieve symbol data",description = "Retrieves symbol data", tags = "Symbol data endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Symbol data found", content = @Content(schema = @Schema(implementation = PagedDataResponse.class), mediaType =  MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "No symbol data found for the requested page.", content = @Content(schema = @Schema(implementation = PagedDataResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping
    public ResponseEntity<?> getSymbols(
            @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "size", defaultValue = "${app.api.symbol.page.default_size}") Integer pageSize
    ) {
        MDC.put(REQUEST_ID, "GET_SYMBOL_DATA");

        log.debug(String.format("original page number: %s | original page size : %s", pageNumber, pageSize));

        PageRequest pageRequest = PageUtil.getPageRequest(pageNumber, pageSize, defaultPageSize, maxPageSize);

        MDC.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
        MDC.put(PAGE_SIZE, String.valueOf(pageRequest.getOffset()));

        Page<Symbol> symbolPage = symbolService.getSymbols(pageRequest);
        HttpStatus responseStatus = symbolPage.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        log.debug(String.format("page content size: %s", symbolPage.getNumberOfElements()));
        log.info("Request processed");

        return ResponseEntity.status(responseStatus).body(new PagedDataResponse(responseStatus.value(), symbolPage));
    }
}
