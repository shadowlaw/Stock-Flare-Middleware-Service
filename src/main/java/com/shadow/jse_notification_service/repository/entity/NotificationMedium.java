package com.shadow.jse_notification_service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "notification_medium")
public class NotificationMedium {

    @Id
    @Column(name = "medium_id")
    private String mediumId;

    @Column(name = "user")
    private String userId;

    @Column(name = "medium_type")
    private String medium_type;
}
