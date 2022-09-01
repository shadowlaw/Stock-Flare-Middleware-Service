package com.shadow.jse_notification_service.repository.entity.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSubscriptionCompositeKey implements Serializable {
    @Column(name = "symbol")
    private String symbol;

    @Column(name = "notif_type")
    private String notificationType;

    @Column(name = "medium_id")
    private String mediumId;
}
