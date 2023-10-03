package com.shadow.jse_middleware_service.controller;


import com.google.gson.Gson;
import com.shadow.jse_middleware_service.constants.NewsType;
import com.shadow.jse_middleware_service.constants.NotificationMediumType;
import com.shadow.jse_middleware_service.constants.PriceTargetType;
import com.shadow.jse_middleware_service.controller.request.NewsSubscriptionRequest;
import com.shadow.jse_middleware_service.controller.request.PriceNotificationRequest;
import com.shadow.jse_middleware_service.util.CustomEnumUtils;
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

import static com.shadow.jse_middleware_service.constants.TestConstants.SUBSCRIBE_ENDPOINT;
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
                            CustomEnumUtils.getNames(NewsType.class))).exists())
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
                            CustomEnumUtils.getNames(NotificationMediumType.class))).exists())
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

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("News subscription Deletion Tests")
    class NewsSubscriptionDeletionTest {
        @ParameterizedTest
        @MethodSource("getInvalidSubscriptionDeletionRequests")
        @DisplayName("Return bad request when invalid request parameters are provided")
        void test_deleteNewsSubscription_givenInvalidRequest_whenValidatingRequest_thenRespondWithBadRequestStatus (String symbolId, String mediumId, String notificationType, String expectedMessage) throws Exception {

            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/1/symbols/%s/news/%s/%s", SUBSCRIBE_ENDPOINT, symbolId, notificationType, mediumId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath(expectedMessage).exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Delete subscription successfully")
        void test_deleteNewsSubscription_givenValidSubscriptionDetails_whenSubscriptionExistsAndRequestIsProcessed_thenRespondWithNoContentStatus() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .delete(String.format("%s/users/1/symbols/SVL/news/DIVDEC/927362871", SUBSCRIBE_ENDPOINT))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        @DisplayName("Subscription exists but medium does not belong to user")
        void test_deleteNewsSubscription_givenValidSubscriptionDetails_whenSubscriptionExistsButMediumDoesNotBelongToUserAndRequestIsProcessed_thenRespondWithNotFoundStatus() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/2/symbols/SVL/news/DIVDEC/927362871", SUBSCRIBE_ENDPOINT))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Subscription details not found\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Subscription does not exists")
        void test_deleteNewsSubscription_givenValidSubscriptionDetails_whenSubscriptionDoesNotExistsAndRequestIsProcessed_thenRespondWithNotFoundStatus() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/2/symbols/BIL/news/DIVDEC/927362871", SUBSCRIBE_ENDPOINT))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Subscription details not found\")]").exists())
                    .andDo(print());
        }

        private Stream<Arguments> getInvalidSubscriptionDeletionRequests(){

            String validMediumId = "123456789";
            String ValidSymbolId = "SVL";

            String expectedSymbolMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"deleteNewsSubscription.symbol: symbol id must be alphanumeric and 3-9 characters in length\")]";
            String expectedMediumIdMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"deleteNewsSubscription.mediumId: Invalid medium id format\")]";
            String expectedNewsTypeMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"deleteNewsSubscription.newsType: Choice Not valid. Valid choices include: "+ CustomEnumUtils.getNames(NewsType.class)+"\")]";

            return Stream.of(
                    // Invalid Symbol Parameters
                    Arguments.of("SCVSDWW.21", validMediumId, NewsType.DIVDEC.toString(), expectedSymbolMessage),
                    Arguments.of("SCVSDWW@2", validMediumId, NewsType.DIVDEC.toString(), expectedSymbolMessage),
                    Arguments.of(validMediumId, validMediumId, NewsType.DIVDEC.toString(), expectedSymbolMessage),
                    Arguments.of("DH", validMediumId, NewsType.DIVDEC.toString(), expectedSymbolMessage),
                    Arguments.of(null, validMediumId, NewsType.DIVDEC.toString(), expectedSymbolMessage),

                    // Invalid Medium ID Parameters
                    Arguments.of(ValidSymbolId, "12345678", NewsType.DIVDEC.toString(), expectedMediumIdMessage),
                    Arguments.of(ValidSymbolId, "abcdefg", NewsType.DIVDEC.toString(), expectedMediumIdMessage),
                    Arguments.of(ValidSymbolId, null, NewsType.DIVDEC.toString(), expectedMediumIdMessage),

                    // Invalid News Type
                    Arguments.of(ValidSymbolId, validMediumId, "ONE", expectedNewsTypeMessage),
                    Arguments.of(ValidSymbolId, validMediumId, null, expectedNewsTypeMessage)
            );
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Price update notification creation test")
    class PriceUpdateCreationTest{

        @ParameterizedTest
        @MethodSource("getInvalidCreatePriceNotificationSubscriptionRequest")
        @DisplayName("Return bad status when user input invalid")
        void test_createPriceNotificationSubscription_givenInvalidRequest_whenValidatingRequest_thenRespondWithBadRequestStatus(String userId, String symbolId, PriceNotificationRequest requestBody, String expectedError) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                        .post(String.format("%s/users/%s/symbols/%s/price", SUBSCRIBE_ENDPOINT, userId, symbolId))
                        .content(gson.toJson(requestBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath(expectedError).exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("User does not exist")
        void test_createPriceNotificationSubscription_givenValidRequest_whenUserDoesNotExists_thenReturnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                        .post(String.format("%s/users/2/symbols/SVL/price", SUBSCRIBE_ENDPOINT))
                        .content(gson.toJson(new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "927362871")))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Unable to find user with id 2\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Medium id does not belong to user")
        void test_createPriceNotificationSubscription_givenValidRequest_whenMediumIdDoesNotBelongToUser_thenReturnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post(String.format("%s/users/3/symbols/SVL/price", SUBSCRIBE_ENDPOINT))
                            .content(gson.toJson(new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "927362871")))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Notification medium not available for use\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Subscription exists but medium id does not belong to user")
        void test_createPriceNotificationSubscription_givenValidRequest_whenSymbolIdDoesNotExist_thenReturnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post(String.format("%s/users/1/symbols/BILL/price", SUBSCRIBE_ENDPOINT))
                            .content(gson.toJson(new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "927362871")))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Unable to find symbol with id BILL\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Subscription already exists")
        void test_createPriceNotificationSubscription_givenValidRequest_whenSubscriptionExists_thenReturnConflict() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post(String.format("%s/users/1/symbols/SVL/price", SUBSCRIBE_ENDPOINT))
                            .content(gson.toJson(new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "927362871")))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"User is already subscribed for notifications\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Successfully create subscription")
        void test_createPriceNotificationSubscription_givenValidRequest_whenRequestHadBeenProcessed_thenReturnCreated() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post(String.format("%s/users/1/symbols/TJH/price", SUBSCRIBE_ENDPOINT))
                            .content(gson.toJson(new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "927362871")))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andDo(print());
        }

        private Stream<Arguments> getInvalidCreatePriceNotificationSubscriptionRequest () {
            String validUserId="1";
            String validSymbol="SVL";
            String validMediumId="123456789";
            String expectedSymbolMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"createPriceNotificationSubscription.symbolId: symbol id must be alphanumeric and 3-9 characters in length\")]";
            String expectedMediumIdMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumId - Invalid medium id format\")]";
            String expectedNewsTypeMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: notificationType - Choice Not valid. Valid choices include: "+CustomEnumUtils.getNames(PriceTargetType.class)+"\")]";

            PriceNotificationRequest priceNotificationRequest = new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), validMediumId);

            return Stream.of(
                    // Invalid Symbol Parameters
                    Arguments.of(validUserId, "SCVSDWW.21", priceNotificationRequest, expectedSymbolMessage),
                    Arguments.of(validUserId, "SCVSDWW@2", priceNotificationRequest, expectedSymbolMessage),
                    Arguments.of(validUserId, "DH", priceNotificationRequest, expectedSymbolMessage),
                    Arguments.of(validUserId, null, priceNotificationRequest, expectedSymbolMessage),

                    // Invalid medium id Parameters
                    Arguments.of(validUserId, validSymbol, new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "12345678"), expectedMediumIdMessage),
                    Arguments.of(validUserId, validSymbol, new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), "abcdefg"), expectedMediumIdMessage),
                    Arguments.of(validUserId, validSymbol, new PriceNotificationRequest(PriceTargetType.PRC_VAL_UP_ALL.toString(), null), "$.errors[?(@.error == \"Validation Error\" && @.message == \"Field: mediumId - must not be blank\")]"),

                    // Invalid price target Parameters
                    Arguments.of(validUserId, validSymbol, new PriceNotificationRequest("OVE", validMediumId), expectedNewsTypeMessage),
                    Arguments.of(validUserId, validSymbol, new PriceNotificationRequest(null, validMediumId), expectedNewsTypeMessage)
            );
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Price Notification subscription Deletion Tests")
    class PriceNotificationSubscriptionDeletionTest {
        @ParameterizedTest
        @MethodSource("getInvalidPriceNotificationSubscriptionDeletionRequests")
        @DisplayName("Return bad request when invalid request parameters are provided")
        void test_deleteNewsSubscription_givenInvalidRequest_whenValidatingRequest_thenRespondWithBadRequestStatus (String symbolId, String mediumId, String notificationType, String expectedMessage) throws Exception {

            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/1/symbols/%s/price/%s/%s", SUBSCRIBE_ENDPOINT, symbolId, notificationType, mediumId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath(expectedMessage).exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Delete subscription successfully")
        void test_deleteNewsSubscription_givenValidSubscriptionDetails_whenSubscriptionExistsAndRequestIsProcessed_thenRespondWithNoContentStatus() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/1/symbols/TJH/price/%s/927362871", SUBSCRIBE_ENDPOINT, PriceTargetType.PRC_VAL_UP_ALL.toString()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        @DisplayName("Subscription exists but medium does not belong to user")
        void test_deleteNewsSubscription_givenValidSubscriptionDetails_whenSubscriptionExistsButMediumDoesNotBelongToUserAndRequestIsProcessed_thenRespondWithNotFoundStatus() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/2/symbols/TJH/price/%s/927362871", SUBSCRIBE_ENDPOINT, PriceTargetType.PRC_VAL_UP_ALL.toString()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Subscription details not found\")]").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Subscription does not exists")
        void test_deleteNewsSubscription_givenValidSubscriptionDetails_whenSubscriptionDoesNotExistsAndRequestIsProcessed_thenRespondWithNotFoundStatus() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete(String.format("%s/users/1/symbols/BIL/news/DIVDEC/927362871", SUBSCRIBE_ENDPOINT))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").hasJsonPath())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errors[?(@.error == \"Error\" && @.message == \"Subscription details not found\")]").exists())
                    .andDo(print());
        }

        private Stream<Arguments> getInvalidPriceNotificationSubscriptionDeletionRequests(){

            String validMediumId = "123456789";
            String ValidSymbolId = "SVL";

            String expectedSymbolMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"deletePriceNotificationSubscription.symbolId: symbol id must be alphanumeric and 3-9 characters in length\")]";
            String expectedMediumIdMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"deletePriceNotificationSubscription.mediumId: Invalid medium id format\")]";
            String expectedNewsTypeMessage = "$.errors[?(@.error == \"Validation Error\" && @.message == \"deletePriceNotificationSubscription.notificationType: Choice Not valid. Valid choices include: "+CustomEnumUtils.getNames(PriceTargetType.class)+"\")]";

            return Stream.of(
                    // Invalid Symbol Parameters
                    Arguments.of("SCVSDWW.21", validMediumId, PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedSymbolMessage),
                    Arguments.of("SCVSDWW@2", validMediumId, PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedSymbolMessage),
                    Arguments.of(validMediumId, validMediumId, PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedSymbolMessage),
                    Arguments.of("DH", validMediumId, PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedSymbolMessage),
                    Arguments.of(null, validMediumId, PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedSymbolMessage),

                    // Invalid Medium ID Parameters
                    Arguments.of(ValidSymbolId, "12345678", PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedMediumIdMessage),
                    Arguments.of(ValidSymbolId, "abcdefg", PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedMediumIdMessage),
                    Arguments.of(ValidSymbolId, null, PriceTargetType.PRC_VAL_UP_ALL.toString(), expectedMediumIdMessage),

                    // Invalid News Type
                    Arguments.of(ValidSymbolId, validMediumId, "ONE", expectedNewsTypeMessage),
                    Arguments.of(ValidSymbolId, validMediumId, null, expectedNewsTypeMessage)
            );
        }
    }
}