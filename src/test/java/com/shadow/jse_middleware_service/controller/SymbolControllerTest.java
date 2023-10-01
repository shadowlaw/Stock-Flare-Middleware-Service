package com.shadow.jse_middleware_service.controller;

import com.google.gson.Gson;
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

import static com.shadow.jse_middleware_service.constants.TestConstants.SYMBOL_ENDPOINT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SymbolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Gson gson = new Gson();

    @Value("${app.api.symbol.page.default_size}")
    private Integer pageDefaultSize;

    @Value("${app.api.symbol.page.max_page_size}")
    private Integer maxPageSize;

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Symbol Data Retrieval Tests")
    class SymbolDataRetrieval {

        @Test
        @DisplayName("Successful call without parameters")
        void test_getSymbols_GivenRequestWithNoQueryParams_whenSymbolDataIsAvailable_thenReturnDefaultPageData () throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                        .get(SYMBOL_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.page.content").hasJsonPath())
                    .andExpect(jsonPath("$.page.numberOfElements").hasJsonPath())
                    .andExpect(jsonPath(String.format("$.page[?(@.numberOfElements <= %s)]", pageDefaultSize)).exists())
                    .andExpect(jsonPath("$.page.pageable.pageNumber").hasJsonPath())
                    .andExpect(jsonPath("$.page.pageable.pageNumber").value(0))
                    .andDo(print());
        }

        @ParameterizedTest
        @MethodSource("getSymbolTestParameters")
        @DisplayName("Test response for query params")
        void test_getSymbolData_givenQueryParameters_whenProcessingRequest_thenReturnGracefulResponse(Integer pageNumber, Integer pageSize, Integer status, Integer expectedPageNumber, Integer expectedPageSize) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get(SYMBOL_ENDPOINT)
                            .param("page", pageNumber.toString())
                            .param("size", pageSize.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(status))
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(status))
                    .andExpect(jsonPath("$.page.content").hasJsonPath())
                    .andExpect(jsonPath(String.format("$.page[?(@.content.length() == %s)]", expectedPageSize)).exists())
                    .andExpect(jsonPath("$.page.numberOfElements").hasJsonPath())
                    .andExpect(jsonPath(String.format("$.page[?(@.numberOfElements <= %s)]", expectedPageSize)).exists())
                    .andExpect(jsonPath("$.page.pageable.pageNumber").hasJsonPath())
                    .andExpect(jsonPath("$.page.pageable.pageNumber").value(expectedPageNumber))
                    .andDo(print());
        }

        private Stream<Arguments> getSymbolTestParameters() {
            return Stream.of(
                    // Given: page number, page size | Expected: return status, page number, page size
                    Arguments.of(-1, 0, HttpStatus.OK.value(), 0, pageDefaultSize),
                    Arguments.of(0, maxPageSize+1, HttpStatus.OK.value(), 0, maxPageSize),
                    Arguments.of(2, maxPageSize, HttpStatus.NOT_FOUND.value(), 2, 0),
                    Arguments.of(0, 5, HttpStatus.OK.value(), 0, 5)
            );
        }

    }

}