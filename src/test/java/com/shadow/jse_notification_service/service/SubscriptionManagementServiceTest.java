package com.shadow.jse_notification_service.service;

import com.shadow.jse_notification_service.exception.ResourceConflictException;
import com.shadow.jse_notification_service.exception.ResourceNotFoundException;
import com.shadow.jse_notification_service.repository.SymbolRepository;
import com.shadow.jse_notification_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}