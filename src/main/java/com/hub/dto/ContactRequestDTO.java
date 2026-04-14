// backend/src/main/java/com/hub/dto/ContactRequestDTO.java
package com.hub.dto;

import com.hub.entity.Contact.GroupName;
import com.hub.entity.ContactInfo.InfoLabel;
import com.hub.entity.ContactInfo.InfoType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    private String avatarUrl;

    private Boolean isFavorite;

    private GroupName groupName;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    @Valid
    private List<ContactInfoDTO> contactInfos;

    @Valid
    private AddressDTO address;

    @Valid
    private List<ReminderDTO> reminders;

    // ---- nested DTOs ----

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ContactInfoDTO {
        @NotNull(message = "Info type is required")
        private InfoType type;

        @NotNull(message = "Info label is required")
        private InfoLabel label;

        @NotBlank(message = "Value is required")
        @Size(max = 100, message = "Value must not exceed 100 characters")
        private String value;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddressDTO {
        @Size(max = 150, message = "Street must not exceed 150 characters")
        private String street;

        @Size(max = 50, message = "City must not exceed 50 characters")
        private String city;

        @Size(max = 50, message = "State must not exceed 50 characters")
        private String state;

        @Size(max = 20, message = "Zip must not exceed 20 characters")
        private String zip;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReminderDTO {
        @NotNull(message = "Reminder date is required")
        private LocalDate date;

        @Size(max = 100, message = "Description must not exceed 100 characters")
        private String description;
    }
}
