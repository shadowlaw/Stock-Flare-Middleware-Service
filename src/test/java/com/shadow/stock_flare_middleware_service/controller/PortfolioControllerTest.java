package com.shadow.stock_flare_middleware_service.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioRequest;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioTradeRequest;
import com.shadow.stock_flare_middleware_service.util.gson_adapters.LocalDateTypeAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.shadow.stock_flare_middleware_service.constants.TestConstants.PORTFOLIO_MANAGEMENT_ENDPOINTS;
import static com.shadow.stock_flare_middleware_service.util.HttpUtils.getBasicAuthenticationHeader;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PortfolioControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).create();

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${app.api.portfolio.dividend.default_date_range_days}")
    long dividendPaymentRange;

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Portfolio Creation Tests")
    class PortfolioCreation {

        @ParameterizedTest
        @MethodSource("createPortfolioInputParams")
        @DisplayName("Create portfolio based on parameters")
        public void testCreatePortfolioGivenInputThenProcessRequestBasedOnParams(String userId, CreatePortfolioRequest request, Integer expectedStatus, String expectedResponse) throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(String.format("%s/%s",PORTFOLIO_MANAGEMENT_ENDPOINTS, userId))
                            .header("Authorization", getBasicAuthenticationHeader(username, password))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request))
                    )
                    .andExpect(status().is(expectedStatus))
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(expectedStatus))
                    .andExpect(jsonPath(expectedResponse).exists())
                    .andDo(print());
        }

        private Stream<Arguments> createPortfolioInputParams() {
            return Stream.of(
                    // Invalid Input Parameters
                    Arguments.of("12Eqw", new CreatePortfolioRequest("N", "1", "STOCK"), HttpStatus.BAD_REQUEST.value(), "$.errors[?(@.error == \"Validation Error\" && @.message == \"createPortfolio.userId: must be numeric\")]"),
                    Arguments.of("12", new CreatePortfolioRequest(null, "1", "STOCK"), HttpStatus.BAD_REQUEST.value(), "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: nickname - Field is required\")]"),
                    Arguments.of("12", new CreatePortfolioRequest("", "1", "STOCK"), HttpStatus.BAD_REQUEST.value(), "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: nickname - Field is required\")]"),
                    Arguments.of("12", new CreatePortfolioRequest("N_)", "1", "STOCK"), HttpStatus.BAD_REQUEST.value(), "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: nickname - Nickname invalid. Nickname must be alphanumeric and can contain _\")]"),

                    // === end invalid parameters

                    // User Not Found
                    Arguments.of("12", new CreatePortfolioRequest("N", "1", "STOCK"), HttpStatus.NOT_FOUND.value(), "$.errors[?(@.error == \"Error\" && @.message == \"User details not found\")]"),

                    // Successful portfolio creation
                    Arguments.of("1", new CreatePortfolioRequest("Test_Portfolio", "1", "STOCK"), HttpStatus.CREATED.value(), "[?(@.name == \"Test_Portfolio\" && @.number == \"1\" && @.type == \"STOCK\")]")
            );
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Portfolio Trade Tests")
    class PortfolioTradeTest{
        @ParameterizedTest
        @MethodSource("createTradeInputParams")
        @DisplayName("Create portfolio trade based on params")
        public void testCreatePortfolioTradeGivenInputTheProcessRequestBasedOnParams(String portfolioId, List<CreatePortfolioTradeRequest> request, Integer expectedStatus, String expectedResponse) throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(String.format("%s/%s/trade", PORTFOLIO_MANAGEMENT_ENDPOINTS, portfolioId))
                            .header("Authorization", getBasicAuthenticationHeader(username, password))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request))
            )
                    .andExpect(status().is(expectedStatus))
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(expectedStatus))
                    .andExpect(jsonPath(expectedResponse).exists())
                    .andDo(print());
        }

        public Stream<Arguments> createTradeInputParams() {
            return Stream.of(
                    // Invalid portfolio ID test params
                    Arguments.of("0b21350-a5e8-4b3d-b900-f2a237d38ba5", Collections.singletonList(new CreatePortfolioRequest()), HttpStatus.BAD_REQUEST.value(), "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioId: Invalid portfolio ID\")]"),
                    // Empty trade request test params
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5", new ArrayList<>(), HttpStatus.BAD_REQUEST.value(), "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest: At least 1 tade is required\")]"),

                    // Invalid symbol Id tests
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("SCVSDWW.21654213", new BigDecimal(1), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].symbolId: Symbol id must be alphanumeric and 2-15 characters in length\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("SCVSDWW@2", new BigDecimal(1), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].symbolId: Symbol id must be alphanumeric and 2-15 characters in length\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("D", new BigDecimal(1), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].symbolId: Symbol id must be alphanumeric and 2-15 characters in length\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest(null, new BigDecimal(1), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].symbolId: This field is required\")]"
                    ),

                    // Invalid number of units params
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL", new BigDecimal(0), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].noOfUnits: Minimum value must be greater than 0 and can be fractional\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL", new BigDecimal("1.342"), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].noOfUnits: Number exceeds allowed value\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL", null, new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].noOfUnits: This field is required\")]"
                    ),

                    // Invalid amount per unit params
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL", new BigDecimal(1), new BigDecimal(0), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].amountPerUnit: Minimum value must be greater than 0 and can be fractional\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL", new BigDecimal(1), new BigDecimal("1.342"), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].amountPerUnit: Number exceeds allowed value\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL",  new BigDecimal(1), null, "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].amountPerUnit: This field is required\")]"
                    ),

                    // Invalid Trade Type Params
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL", new BigDecimal(1), new BigDecimal(1), "INVALID", LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].type: Choice Not valid. Valid choices include: BUY, SELL\")]"
                    ),
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL",  new BigDecimal(1), new BigDecimal(1), null, LocalDate.now(), null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].type: This field is required\")]"
                    ),

                    // Invalid Date Params
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL",  new BigDecimal(1), new BigDecimal(1), "BUY", null, null, null, null)),
                            HttpStatus.BAD_REQUEST.value(),
                            "$.errors[?(@.error == \"Validation Error\" && @.message == \"createTrade.portfolioTradeRequest[0].transactionDate: This field is required\")]"
                    ),

                    // Failure params if portfolio does not exist
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba4",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL",  new BigDecimal(1), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.NOT_FOUND.value(),
                            "$.errors[?(@.error == \"Error\" && @.message == \"Unknown portfolio ID\")]"
                    ),

                    //Successful portfolio trade created
                    Arguments.of("0eb21350-a5e8-4b3d-b900-f2a237d38ba5",
                            Collections.singletonList(new CreatePortfolioTradeRequest("GHL",  new BigDecimal(1), new BigDecimal(1), "BUY", LocalDate.now(), null, null, null)),
                            HttpStatus.CREATED.value(),
                            "$.trades[?(@.symbol == \"GHL\")]"
                    )
            );

        }
    }


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Get Dividend Payments Tests")
    class GetDividendPayments{

        @ParameterizedTest
        @MethodSource("getDividendPaymentsParams")
        @DisplayName("Retrieve dividend payment based on param")
        public void testGetDividendPaymentsGivenInputTheProcessRequestBasedOnParams(
                String symbol, String startDate, String endDate, String portfolioId, Integer expectedStatus, String expectedResponse
        ) throws Exception {
            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("symbol", symbol);
            params.add("start-date", startDate);
            params.add("end-date", endDate);

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(String.format("%s/%s/dividend", PORTFOLIO_MANAGEMENT_ENDPOINTS, portfolioId))
                                    .header("Authorization", getBasicAuthenticationHeader(username, password))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .params(params)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().is(expectedStatus))
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(expectedStatus))
                    .andExpect(jsonPath(expectedResponse).exists())
                    .andDo(print());
        }

        public Stream<Arguments> getDividendPaymentsParams() {
            return Stream.of(
                    // unknown portfolio id test params
                    Arguments.of("SVL", "2023-01-01", "2023-01-01", "0eb21350-a5e8-4b3d-b900-f2a237d38ba4", HttpStatus.NOT_FOUND.value(), "$.errors[?(@.error == \"Error\" && @.message == \"Unknown portfolio ID\")]"),

                    // date range too large test params
                    Arguments.of("SVL", "2023-01-01", LocalDate.of(2023, 1, 1).plusDays(dividendPaymentRange+1).toString(), "0eb21350-a5e8-4b3d-b900-f2a237d38ba5", HttpStatus.BAD_REQUEST.value(), String.format("$.errors[?(@.error == \"Error\" && @.message == \"Date range provided greater then [%s] days\")]", dividendPaymentRange))
            );
        }
    }
}