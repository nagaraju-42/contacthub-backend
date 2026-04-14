// backend/src/main/java/com/hub/entity/Reminder.java
package com.hub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reminders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", length = 100)
    private String description;
}
