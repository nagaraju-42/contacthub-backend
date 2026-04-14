// backend/src/main/java/com/hub/entity/ContactInfo.java
package com.hub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contact_info")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private InfoType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "label", length = 10, nullable = false)
    private InfoLabel label;

    @Column(name = "value", length = 100, nullable = false)
    private String value;

    public enum InfoType { PHONE, EMAIL }
    public enum InfoLabel { WORK, HOME, MOBILE }
}
