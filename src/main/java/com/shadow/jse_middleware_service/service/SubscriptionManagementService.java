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

    public void createNewsNotification(String user_id, String symbol, String newsType, String mediumType, String medium_id ) {
        if (!userRepository.existsById(Integer.parseInt(user_id))) {
            throw new ResourceNotFoundException(String.format("Unable to find user with id %s", user_id), null);
        }

        if (!symbolRepository.existsById(symbol)) {
            throw new ResourceNotFoundException(String.format("Unable to find symbol with id %s", symbol), null);
        }

        if (!notificationMediumService.notificationMediumExist(medium_id)) {
            notificationMediumService.createNotificationMedium(medium_id, user_id, mediumType);
        } else if (!notificationMediumService.isMediumOwnedByUser(user_id, medium_id)) {
            throw new ResourceConflictException("Notification medium not available for use", null);
        }

        if (notificationSubscriptionService.isSubscribed(newsType, symbol, medium_id)) {
            throw new ResourceConflictException("User is already subscribed for notifications", null);
        }

        notificationSubscriptionService.subscribe(newsType, symbol, medium_id);
    }

    public void deleteNewsNotification(String userId, String symbol, String notificationType, String mediumId) {
        deleteSubscription(userId, symbol, notificationType, mediumId);
    }

    public void createPriceNotification(String userId, String symbolId, String notificationType, String mediumId) {
        log.info("creating price notification");

        if (!userRepository.existsById(Integer.parseInt(userId))) {
            throw new ResourceNotFoundException(String.format("Unable to find user with id %s", userId), null);
        }

        if (!symbolRepository.existsById(symbolId)) {
            throw new ResourceNotFoundException(String.format("Unable to find symbol with id %s", symbolId), null);
        }

        if (!notificationMediumService.isMediumOwnedByUser(userId, mediumId)) {
            throw new ResourceNotFoundException("Notification medium not available for use", null);
        }

        if (notificationSubscriptionService.isSubscribed(notificationType, symbolId, mediumId)) {
            throw new ResourceConflictException("User is already subscribed for notifications", null);
        }

        notificationSubscriptionService.subscribe(notificationType, symbolId, mediumId);

        log.info("price notification created");
    }

    public void deletePriceNotification(String userId, String symbolId, String notificationType, String mediumId) {
        deleteSubscription(userId, symbolId, notificationType, mediumId);
    }

    private void deleteSubscription(String userId, String symbolId, String notificationType, String mediumId) {
        log.info(String.format("Deleting notification subscription [ %s - %s - %s ]", notificationType, symbolId, mediumId));

        if (!notificationMediumService.isMediumOwnedByUser(userId, mediumId)) {
            log.error(String.format("medium [%s] does not belong to user [%s]", mediumId, userId));
            throw new ResourceNotFoundException("Subscription details not found", null);
        }

        Optional<NotificationSubscription> subscriptionOptional = notificationSubscriptionService.getSubscription(notificationType, symbolId, mediumId);

        if (subscriptionOptional.isEmpty()) {
            log.error(String.format("Notification subscription [ %s - %s - %s ] does not exist", notificationType, symbolId, mediumId));
            throw new ResourceNotFoundException("Subscription details not found", null);
        }

        notificationSubscriptionService.deleteSubscription(subscriptionOptional.get());

        log.info(String.format("Notification subscription [ %s - %s - %s ] deleted", notificationType, symbolId, mediumId));
    }
}
