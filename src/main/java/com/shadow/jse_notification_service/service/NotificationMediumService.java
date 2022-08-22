package com.shadow.jse_notification_service.service;

import com.shadow.jse_notification_service.repository.NotificationMediumRepository;
import com.shadow.jse_notification_service.repository.entity.NotificationMedium;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMediumService {

    @Autowired
    private NotificationMediumRepository notificationMediumRepository;
    public void createNotificationMedium (String medium_id, String user_id, String mediumType){
        NotificationMedium notificationMedium = new NotificationMedium(medium_id, user_id, mediumType);

        notificationMediumRepository.save(notificationMedium);
    }

    public boolean notificationMediumExist(String medium_id) {
        return notificationMediumRepository.existsById(medium_id);
    }

    public boolean isMediumOwnedByUser(String user_id, String medium_id) {
            return notificationMediumRepository.findByUserIdAndMediumId(user_id, medium_id).isPresent();
    }

}
