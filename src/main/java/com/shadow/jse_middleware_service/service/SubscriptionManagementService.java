package com.shadow.jse_middleware_service.service;

import com.shadow.jse_middleware_service.constants.SubscriptionType;
import com.shadow.jse_middleware_service.exception.ResourceConflictException;
import com.shadow.jse_middleware_service.exception.ResourceNotFoundException;
import com.shadow.jse_middleware_service.repository.SymbolRepository;
import com.shadow.jse_middleware_service.repository.UserRepository;
import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SubscriptionManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private NotificationMediumService notificationMediumService;

    @Autowired
    private NotificationSubscriptionService notificationSubscriptionService;

    public Page<NotificationSubscription> getNotificationSubscription(String mediumId, PageRequest pageRequest) {
        return notificationSubscriptionService.getSubscriptionsByPage(mediumId, pageRequest);
    }

    public Page<NotificationSubscription> getNotificationSubscription(String mediumId, String subscriptionType, PageRequest pageRequest) {

        return notificationSubscriptionService.getSubscriptionsByPage(mediumId, SubscriptionType.getSubTypes(SubscriptionType.valueOf(subscriptionType.toUpperCase())), pageRequest);
    }

    public void createNewsNotification(String medium_id, String symbol, String newsType) {

        if (!notificationMediumService.notificationMediumExist(medium_id)) {
            throw new ResourceNotFoundException("medium not available for use", null);
        }

        if (!symbolRepository.existsById(symbol)) {
            throw new ResourceNotFoundException(String.format("Unable to find symbol with id %s", symbol), null);
        }

        if (notificationSubscriptionService.isSubscribed(newsType, symbol, medium_id)) {
            throw new ResourceConflictException("User is already subscribed for notifications", null);
        }

        notificationSubscriptionService.subscribe(newsType, symbol, medium_id);
    }

    public void deleteNewsNotification(String symbol, String notificationType, String mediumId) {
        deleteSubscription(symbol, notificationType, mediumId);
    }

    public void createPriceNotification(String mediumId, String symbolId, String notificationType) {
        log.info("creating price notification");

        if (!notificationMediumService.notificationMediumExist(mediumId)) {
            throw new ResourceNotFoundException("Notification medium not available for use", null);
        }

        if (!symbolRepository.existsById(symbolId)) {
            throw new ResourceNotFoundException(String.format("Unable to find symbol with id %s", symbolId), null);
        }

        if (notificationSubscriptionService.isSubscribed(notificationType, symbolId, mediumId)) {
            throw new ResourceConflictException("User is already subscribed for notifications", null);
        }

        notificationSubscriptionService.subscribe(notificationType, symbolId, mediumId);

        log.info("price notification created");
    }

    public void deletePriceNotification(String symbolId, String notificationType, String mediumId) {
        deleteSubscription(symbolId, notificationType, mediumId);
    }

    private void deleteSubscription(String symbolId, String notificationType, String mediumId) {
        log.info(String.format("Deleting notification subscription [ %s - %s - %s ]", notificationType, symbolId, mediumId));

        Optional<NotificationSubscription> subscriptionOptional = notificationSubscriptionService.getSubscription(notificationType, symbolId, mediumId);

        if (subscriptionOptional.isEmpty()) {
            log.error(String.format("Notification subscription [ %s - %s - %s ] does not exist", notificationType, symbolId, mediumId));
            throw new ResourceNotFoundException("Subscription details not found", null);
        }

        notificationSubscriptionService.deleteSubscription(subscriptionOptional.get());

        log.info(String.format("Notification subscription [ %s - %s - %s ] deleted", notificationType, symbolId, mediumId));
    }
}
