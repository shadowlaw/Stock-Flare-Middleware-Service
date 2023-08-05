package com.shadow.jse_middleware_service.service;

import com.shadow.jse_middleware_service.repository.NotificationSubscriptionRepository;
import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSubscriptionServiceTest {

    @Mock
    private NotificationSubscriptionRepository notificationSubscriptionRepository;

    @InjectMocks
    private NotificationSubscriptionService notificationSubscriptionService;

    @Test
    void testGetSubscription_givenASubscriptionID_whenSubscriptionExists_thenReturnSubscriptionOptional() {

        NotificationSubscription subscription = new NotificationSubscription(new NotificationSubscriptionCompositeKey("SVL", "DIVDEC", "123456789"));

        when(notificationSubscriptionRepository.getById(any(NotificationSubscriptionCompositeKey.class))).thenReturn(subscription);
        when(notificationSubscriptionRepository.existsById(any(NotificationSubscriptionCompositeKey.class))).thenReturn(true);

        Optional<NotificationSubscription> result = notificationSubscriptionService.getSubscription("", "", "");

        assertTrue(result.isPresent(), "No subscription present");
        assertEquals(subscription, result.get());
    }

    @Test
    void testGetSubscription_givenASubscriptionID_whenSubscriptionDoesNotExists_thenReturnEmptyOptional() {

        when(notificationSubscriptionRepository.existsById(any(NotificationSubscriptionCompositeKey.class))).thenReturn(false);

        Optional<NotificationSubscription> result = notificationSubscriptionService.getSubscription("", "", "");

        assertTrue(result.isEmpty(), "Unexpected subscription present");
    }
}