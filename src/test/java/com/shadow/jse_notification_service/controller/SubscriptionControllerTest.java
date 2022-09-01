package com.shadow.jse_notification_service.controller;


import com.google.gson.Gson;
import com.shadow.jse_notification_service.constants.NewsType;
import com.shadow.jse_notification_service.constants.NotificationMediumType;
import com.shadow.jse_notification_service.controller.request.NewsSubscriptionRequest;
import org.apache.commons.lang3.EnumUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.stream.Stream;

import static com.shadow.jse_notification_service.constants.TestConstants.SUBSCRIBE_ENDPOINT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    Gson gson = new Gson();

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("News Subscription Tests")
    class NewsSubscriptionTest {

        @Test
        @DisplayName("Create new notification subscription")
        @Order(1)
        void test_newsSubscription_givenValidUserIdSymbolAndUnregisteredNotificationMedium_whenRequestHasBeenProcessed_thenRespondWithCreatedStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                    .post(SUBSCRIBE_ENDPOINT + "/users/1/symbols/SVL/news")
                    .content(gson.toJson(requestBody))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andDo(print());
        }

        @Test
        @DisplayName("Return conflict when attempting to create existing notification subscription")
        @Order(2)
        void test_newsSubscription_givenExistingDetails_whenRequestHasBeenProcessed_thenRespondWithConflictStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/1/symbols/SVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"User is already subscribed for notifications\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Return not found when symbol id does not exist")
        void test_newsSubscription_givenUnknownSymbolId_whenRequestHasBeenProcessed_thenRespondWithNotFoundStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/1/symbols/SVVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Unable to find symbol with id SVVL\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Return not found when user id does not exist")
        void test_newsSubscription_givenUnknownUserId_whenRequestHasBeenProcessed_thenRespondWithNotFoundStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/2/symbols/SVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Unable to find user with id 2\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Return conflict when notification medium does belong to user id")
        @Order(3)
        void test_newsSubscription_givenMismatchedUserIdAndMediumId_whenRequestHasBeenProcessed_thenRespondWithConflictStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/3/symbols/SVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Notification medium not available for use\")]").exists())
                    .andDo(print());
        }

        @ParameterizedTest
        @MethodSource("getInvalidSymbolIds")
        @DisplayName("Return bad request when invalid symbol id format is provided")
        void test_newsSubscription_givenInvalidSymbolIdFormat_whenRequestIsValidating_thenRespondWithBadRequestStatus (String symbolId) throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(String.format("%s/users/1/symbols/%s/news", SUBSCRIBE_ENDPOINT, symbolId))
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Validation Error\" && @.message == \"newsSubscribe.symbol: symbol id must be alphanumeric and 3-9 characters in length\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Return bad request when unknown news type is provided")
        void test_newsSubscription_givenInvalidNewsType_whenRequestIsValidating_thenRespondWithBadRequestStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest("BAD_NEWS_TYPE", NotificationMediumType.TELEGRAM.toString(), "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/1/symbols/SVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath(String.format("$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: newsType - Choice Not valid. Valid choices include: %s\")]",
                            EnumUtils.getEnumList(NewsType.class).toString().replace("[", "").replace("]", ""))).exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Return bad request when unknown notification medium type is provided")
        void test_newsSubscription_givenInvalidMediumType_whenRequestIsValidating_thenRespondWithBadRequestStatus () throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), "BAD_MEDIUM_TYPE", "123456789");

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/1/symbols/SVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath(String.format("$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumType - Choice Not valid. Valid choices include: %s\")]",
                            EnumUtils.getEnumList(NotificationMediumType.class).toString().replace("[", "").replace("]", ""))).exists())
                    .andDo(print());
        }

        @ParameterizedTest
        @MethodSource("getInvalidMediumIds")
        @DisplayName("Return bad request when invalid medium id format is provided")
        void test_newsSubscription_givenInvalidMediumIdFormat_whenRequestIsValidating_thenRespondWithBadRequestStatus (String mediumId, String expected) throws Exception {

            NewsSubscriptionRequest requestBody = new NewsSubscriptionRequest(NewsType.DIVDEC.toString(), NotificationMediumType.TELEGRAM.toString(), mediumId);

            mockMvc.perform(MockMvcRequestBuilders
                            .post(SUBSCRIBE_ENDPOINT + "/users/1/symbols/SVL/news")
                            .content(gson.toJson(requestBody))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath(expected).exists())
                    .andDo(print());
        }

        private Stream<Arguments> getInvalidMediumIds () {
            return Stream.of(
                    Arguments.of("12345678", "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumId - Invalid medium id format\")]"),
                    Arguments.of("abcdefg", "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumId - Invalid medium id format\")]"),
                    Arguments.of("", "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumId - Invalid medium id format\")]"),
                    Arguments.of((Object) null, "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumId - must not be blank\")]")
            );
        }
        private Stream<Arguments> getInvalidSymbolIds () {
            return Stream.of(
                    Arguments.of("SCVSDWW.21"),
                    Arguments.of("SCVSDWW@2"),
                    Arguments.of("123456789"),
                    Arguments.of("DH"),
                    Arguments.of((Object) null)
            );
        }
    }
}