package com.shadow.jse_notification_service.service;

import com.shadow.jse_notification_service.exception.ResourceConflictException;
import com.shadow.jse_notification_service.exception.ResourceNotFoundException;
import com.shadow.jse_notification_service.repository.SymbolRepository;
import com.shadow.jse_notification_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private NotificationMediumService notificationMediumService;

    @Autowired
    private NotificationSubscriptionService notificationSubscriptionService;

    Logger logger = LoggerFactory.getLogger(SubscriptionManagementService.class);

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
}
