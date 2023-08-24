package com.shadow.jse_middleware_service.service;

import com.shadow.jse_middleware_service.exception.ResourceConflictException;
import com.shadow.jse_middleware_service.exception.ResourceNotFoundException;
import com.shadow.jse_middleware_service.repository.SymbolRepository;
import com.shadow.jse_middleware_service.repository.UserRepository;
import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SubscriptionManagementServiceTest {

    @InjectMocks
    private SubscriptionManagementService subscriptionManagementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SymbolRepository symbolRepository;

    @Mock
    private NotificationMediumService notificationMediumService;

    @Mock
    private NotificationSubscriptionService notificationSubscriptionService;

    @Test
    void test_createNewsNotification_givenUserId_whenUserNotFound_thenThrowResourceNotFoundException() {
        String user_id = "1";

        when(userRepository.existsById(any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.createNewsNotification(user_id, null, null, null, null);
        });

        assertTrue(exception.getMessage().contains(String.format("Unable to find user with id %s", user_id)),
                "Expected and actual message do not match");

    }

    @Test
    void test_createNewsNotification_givenSymbolId_whenSymbolNotFound_thenThrowResourceNotFoundException() {
        String user_id = "1";
        String symbol = "SVVC";

        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.createNewsNotification(user_id, symbol, null, null, null);
        });

        assertTrue(exception.getMessage().contains(String.format("Unable to find symbol with id %s", symbol)),
                "Expected and actual message do not match");
    }

    @Test
    void test_createNewsNotification_givenMismatchedUserIdAndMediumID_whenUserDoesNotOwnMedium_thenThrowResourceConflictException() {
        String user_id = "1";
        String symbol = "SVVC";
        String medium_id = "12345678";

        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.notificationMediumExist(any())).thenReturn(true);
        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(false);

        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> {
            subscriptionManagementService.createNewsNotification(user_id, symbol, null, null,medium_id);
        });

        assertTrue(exception.getMessage().contains("Notification medium not available for use"),
                "Expected and actual message do not match");
    }

    @Test
    void test_createNewsNotification_givenValidInputForSubscription_whenUserIsSubscribed_thenThrowResourceConflictException () {

        String user_id = "1";

        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.notificationMediumExist(any())).thenReturn(true);
        when(notificationMediumService.isMediumOwnedByUser(any(),any())).thenReturn(true);
        when(notificationSubscriptionService.isSubscribed(any(), any(), any())).thenReturn(true);

        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> {
            subscriptionManagementService.createNewsNotification(user_id, null, null, null, null);
        });

        assertTrue(exception.getMessage().contains("User is already subscribed for notifications"),
                "Expected and actual message do not match");

    }

    @Test
    void test_createNewsNotification_givenValidInputForSubscription_whenMediumIdNotFound_thenCreateMediumAndUserIdAssignment() {
        String user_id = "1";
        String medium_id = "12345678";
        String medium_type = "12345678";

        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.notificationMediumExist(any())).thenReturn(false);
        when(notificationSubscriptionService.isSubscribed(any(), any(), any())).thenReturn(false);


        ArgumentCaptor<String> actual_user_id = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> actual_medium_id = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> actual_medium_type = ArgumentCaptor.forClass(String.class);


        subscriptionManagementService.createNewsNotification(user_id, null, null, medium_type, medium_id);

        verify(notificationMediumService).createNotificationMedium(
                actual_medium_id.capture(), actual_user_id.capture(), actual_medium_type.capture());

        assertEquals(medium_id, actual_medium_id.getValue());
        assertEquals(medium_type, actual_medium_type.getValue());
        assertEquals(user_id, actual_user_id.getValue());
    }

    @Test
    void test_createNewsNotification_givenValidInputForSubscription_whenMediumIdIsFoundAndUserIsNotSubscribed_thenSubscribeTheUserForNotifications() {
        String user_id = "1";
        String medium_id = "12345678";
        String symbol = "SVL";
        String news_type = "NEWS_TYPE";

        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.notificationMediumExist(any())).thenReturn(true);
        when(notificationMediumService.isMediumOwnedByUser(any(),any())).thenReturn(true);
        when(notificationSubscriptionService.isSubscribed(any(), any(), any())).thenReturn(false);


        ArgumentCaptor<String> actual_news_type = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> actual_medium_id = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> actual_symbol = ArgumentCaptor.forClass(String.class);


        subscriptionManagementService.createNewsNotification(user_id, symbol, news_type, null, medium_id);

        verify(notificationSubscriptionService).subscribe(
                actual_news_type.capture(), actual_symbol.capture(), actual_medium_id.capture());

        assertEquals(medium_id, actual_medium_id.getValue());
        assertEquals(symbol, actual_symbol.getValue());
        assertEquals(news_type, actual_news_type.getValue());
    }

    @Test
    void testDeleteNewsSubscription_givenSubscriptionData_whenSubscriptionDoesNotExist_thenThrowResourceNotFoundException () {

        String expectedExceptionMessage = "Subscription details not found";

        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(true);
        when(notificationSubscriptionService.getSubscription(any(), any(), any())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
           subscriptionManagementService.deleteNewsNotification("1","SVL","DIVDEC", "123456789");
        });

        assertEquals(exception.getMessage(), expectedExceptionMessage);
    }

    @Test
    void testDeleteNewsSubscription_givenSubscriptionData_whenSubscriptionExistsButDoesNotBelongToUser_thenThrowResourceNotFoundException () {

        String expectedExceptionMessage = "Subscription details not found";

        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.deleteNewsNotification("1","SVL","DIVDEC", "123456789");
        });

        assertEquals(exception.getMessage(), expectedExceptionMessage);
    }

    @Test
    void testDeleteNewsSubscription_givenSubscriptionData_whenSubscriptionExistsAndBelongsToUser_thenDeleteSubscription() {

        String symbol = "SVL";
        String notif_type = "DIVDEV";
        String medium_id = "123456789";

        NotificationSubscription subscription = new NotificationSubscription(new NotificationSubscriptionCompositeKey(symbol, notif_type, medium_id));
        ArgumentCaptor<NotificationSubscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(NotificationSubscription.class);

        when(notificationSubscriptionService.getSubscription(any(), any(), any())).thenReturn(Optional.of(subscription));
        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(true);

        subscriptionManagementService.deleteNewsNotification("", symbol, notif_type, medium_id);
        verify(notificationSubscriptionService).deleteSubscription(subscriptionArgumentCaptor.capture());

        assertEquals(subscription, subscriptionArgumentCaptor.getValue());
    }

    @Test
    void testCreatePriceNotification_givenInputParameters_whenUserIdIsNotFoundInDB_thenThrowResourceNotFoundException(){
        String userId = "1";

        when(userRepository.existsById(any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.createPriceNotification(userId, "", "", "");
        });

        assertEquals(String.format("Unable to find user with id %s", userId), exception.getMessage());
    }

    @Test
    void testCreatePriceNotification_givenInputParameters_whenSymbolIdIsNotFoundInDB_thenThrowResourceNotFoundException(){
        String symbolId = "1";

        when(userRepository.existsById(any())).thenReturn(true);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.createPriceNotification("1", symbolId, "", "");
        });

        assertEquals(String.format("Unable to find symbol with id %s", symbolId), exception.getMessage());
    }

    @Test
    void testCreatePriceNotification_givenInputParameters_whenMediumIdIsNotFoundInDB_thenThrowResourceNotFoundException(){
        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.createPriceNotification("1", "", "", "");
        });

        assertEquals("Notification medium not available for use", exception.getMessage());
    }

    @Test
    void testCreatePriceNotification_givenInputParameters_whenSubscriptionExists_thenThrowResourceConflictException(){
        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(true);
        when(notificationSubscriptionService.isSubscribed(any(), any(), any())).thenReturn(true);


        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> {
            subscriptionManagementService.createPriceNotification("1", "", "", "");
        });

        assertEquals("User is already subscribed for notifications", exception.getMessage());
    }

    @Test
    void testCreatePriceNotification_givenValidInputParameters_whenSubscriptionDoesNotExist_thenCreateSubscription() {
        String symbol = "SVL";
        String notif_type = "DIVDEV";
        String medium_id = "123456789";

        when(userRepository.existsById(any())).thenReturn(true);
        when(symbolRepository.existsById(any())).thenReturn(true);
        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(true);
        when(notificationSubscriptionService.isSubscribed(any(), any(), any())).thenReturn(false);

        ArgumentCaptor<String> notificationTypeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> symbolCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> mediumIdCaptor = ArgumentCaptor.forClass(String.class);

        subscriptionManagementService.createPriceNotification("1", symbol, notif_type, medium_id);
        verify(notificationSubscriptionService).subscribe(notificationTypeCaptor.capture(), symbolCaptor.capture(), mediumIdCaptor.capture());

        assertEquals(notif_type, notificationTypeCaptor.getValue());
        assertEquals(symbol, symbolCaptor.getValue());
        assertEquals(medium_id, mediumIdCaptor.getValue());
    }

    @Test
    void testDeletePriceNotificationSubscription_givenSubscriptionData_whenMediumIdDoesNotBelongToUser_thenThrowResourceNotFoundException () {

        String expectedExceptionMessage = "Subscription details not found";

        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.deletePriceNotification("1","SVL","DIVDEC", "123456789");
        });

        assertEquals(exception.getMessage(), expectedExceptionMessage);
    }

    @Test
    void testDeletePriceNotificationSubscription_givenSubscriptionData_whenSubscriptionExistsAndBelongsToUser_thenDeleteSubscription() {

        String symbol = "SVL";
        String notif_type = "DIVDEV";
        String medium_id = "123456789";

        NotificationSubscription subscription = new NotificationSubscription(new NotificationSubscriptionCompositeKey(symbol, notif_type, medium_id));
        ArgumentCaptor<NotificationSubscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(NotificationSubscription.class);

        when(notificationSubscriptionService.getSubscription(any(), any(), any())).thenReturn(Optional.of(subscription));
        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(true);

        subscriptionManagementService.deletePriceNotification("", symbol, notif_type, medium_id);
        verify(notificationSubscriptionService).deleteSubscription(subscriptionArgumentCaptor.capture());

        assertEquals(subscription, subscriptionArgumentCaptor.getValue());
    }

    @Test
    void testDeletePriceNotificationSubscription_givenSubscriptionData_whenSubscriptionDoesNotExist_thenThrowResourceNotFoundException () {

        String expectedExceptionMessage = "Subscription details not found";

        when(notificationMediumService.isMediumOwnedByUser(any(), any())).thenReturn(true);
        when(notificationSubscriptionService.getSubscription(any(), any(), any())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionManagementService.deletePriceNotification("1","SVL","DIVDEC", "123456789");
        });

        assertEquals(exception.getMessage(), expectedExceptionMessage);
    }
}