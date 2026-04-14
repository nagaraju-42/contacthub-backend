// backend/src/main/java/com/hub/dto/ContactResponseDTO.java
package com.hub.dto;

import com.hub.entity.Contact.GroupName;
import com.hub.entity.ContactInfo.InfoLabel;
import com.hub.entity.ContactInfo.InfoType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Boolean isFavorite;
    private GroupName groupName;
    private String notes;
    private LocalDateTime createdAt;
    private List<ContactInfoDTO> contactInfos;
    private AddressDTO address;
    private List<ReminderDTO> reminders;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ContactInfoDTO {
        private UUID id;
        private InfoType type;
        private InfoLabel label;
        private String value;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddressDTO {
        private UUID id;
        private String street;
        private String city;
        private String state;
        private String zip;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReminderDTO {
        private UUID id;
        private LocalDate date;
        private String description;
    }
}
