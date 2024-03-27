package com.shadow.stock_flare_middleware_service.controller;

import com.google.gson.Gson;
import com.shadow.stock_flare_middleware_service.controller.request.CreatePortfolioRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    Gson gson = new Gson();

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

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
}