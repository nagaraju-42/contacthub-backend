// backend/src/main/java/com/hub/service/ContactMapper.java
package com.hub.service;

import com.hub.dto.ContactRequestDTO;
import com.hub.dto.ContactResponseDTO;
import com.hub.entity.Address;
import com.hub.entity.Contact;
import com.hub.entity.ContactInfo;
import com.hub.entity.Reminder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactMapper {

    public ContactResponseDTO toResponse(Contact contact) {
        return ContactResponseDTO.builder()
            .id(contact.getId())
            .firstName(contact.getFirstName())
            .lastName(contact.getLastName())
            .avatarUrl(contact.getAvatarUrl())
            .isFavorite(contact.getIsFavorite())
            .groupName(contact.getGroupName())
            .notes(contact.getNotes())
            .createdAt(contact.getCreatedAt())
            .contactInfos(mapContactInfos(contact.getContactInfos()))
            .address(mapAddress(contact.getAddress()))
            .reminders(mapReminders(contact.getReminders()))
            .build();
    }

    private List<ContactResponseDTO.ContactInfoDTO> mapContactInfos(List<ContactInfo> infos) {
        if (infos == null) return Collections.emptyList();
        return infos.stream()
            .map(i -> ContactResponseDTO.ContactInfoDTO.builder()
                .id(i.getId())
                .type(i.getType())
                .label(i.getLabel())
                .value(i.getValue())
                .build())
            .collect(Collectors.toList());
    }

    private ContactResponseDTO.AddressDTO mapAddress(Address address) {
        if (address == null) return null;
        return ContactResponseDTO.AddressDTO.builder()
            .id(address.getId())
            .street(address.getStreet())
            .city(address.getCity())
            .state(address.getState())
            .zip(address.getZip())
            .build();
    }

    private List<ContactResponseDTO.ReminderDTO> mapReminders(List<Reminder> reminders) {
        if (reminders == null) return Collections.emptyList();
        return reminders.stream()
            .map(r -> ContactResponseDTO.ReminderDTO.builder()
                .id(r.getId())
                .date(r.getDate())
                .description(r.getDescription())
                .build())
            .collect(Collectors.toList());
    }

    public void applyRequest(ContactRequestDTO req, Contact contact) {
        contact.setFirstName(req.getFirstName());
        contact.setLastName(req.getLastName());
        if (req.getAvatarUrl() != null) contact.setAvatarUrl(req.getAvatarUrl());
        if (req.getIsFavorite() != null) contact.setIsFavorite(req.getIsFavorite());
        if (req.getGroupName() != null) contact.setGroupName(req.getGroupName());
        contact.setNotes(req.getNotes());

        // Contact infos
        contact.getContactInfos().clear();
        if (req.getContactInfos() != null) {
            req.getContactInfos().forEach(dto -> {
                ContactInfo ci = ContactInfo.builder()
                    .contact(contact)
                    .type(dto.getType())
                    .label(dto.getLabel())
                    .value(dto.getValue())
                    .build();
                contact.getContactInfos().add(ci);
            });
        }

        // Address
        if (req.getAddress() != null) {
            Address addr = contact.getAddress();
            if (addr == null) {
                addr = new Address();
                addr.setContact(contact);
                contact.setAddress(addr);
            }
            addr.setStreet(req.getAddress().getStreet());
            addr.setCity(req.getAddress().getCity());
            addr.setState(req.getAddress().getState());
            addr.setZip(req.getAddress().getZip());
        } else {
            contact.setAddress(null);
        }

        // Reminders
        contact.getReminders().clear();
        if (req.getReminders() != null) {
            req.getReminders().forEach(dto -> {
                Reminder r = Reminder.builder()
                    .contact(contact)
                    .date(dto.getDate())
                    .description(dto.getDescription())
                    .build();
                contact.getReminders().add(r);
            });
        }
    }
}
