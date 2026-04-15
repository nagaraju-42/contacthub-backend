// backend/src/main/java/com/hub/service/ContactService.java
package com.hub.service;

import com.hub.dto.ContactRequestDTO;
import com.hub.dto.ContactResponseDTO;
import com.hub.dto.PagedResponseDTO;
import com.hub.entity.Contact;
import com.hub.entity.User;
import com.hub.exception.ResourceNotFoundException;
import com.hub.repository.ContactRepository;
import com.hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final ContactMapper mapper;

    // Resolve user — principal is now the UUID string from the JWT subject
    private UUID resolveUserId(String principal) {
        return UUID.fromString(principal);
    }

    private User getOrCreateMockUser() {
        // This method is no longer needed for production, but kept for backward compatibility
        User user = userRepository.findAll().stream().findFirst().orElseGet(() -> {
            User newUser = User.builder()
                .name("Default User")
                .email("default@contacthub.dev")
                .passwordHash("$2a$10$placeholder")
                .build();
            return userRepository.save(newUser);
        });
        return user;
    }

    @Transactional(readOnly = true)
    public PagedResponseDTO<ContactResponseDTO> getAll(String principal,
                                                        int page, int size,
                                                        String search,
                                                        String group,
                                                        Boolean favorite) {
        UUID userId = resolveUserId(principal);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contactPage;

        // Determine which query to use based on filters
        if (favorite != null && favorite) {
            // Favorite filter only
            if (search != null && !search.isBlank()) {
                contactPage = contactRepository.searchByUserIdFavoriteAndName(userId, search.trim(), pageable);
            } else {
                contactPage = contactRepository.findByUserIdAndFavorite(userId, pageable);
            }
        } else if (group != null && !group.isBlank()) {
            // Group filter
            if (search != null && !search.isBlank()) {
                contactPage = contactRepository.searchByUserIdGroupNameAndName(userId, group, search.trim(), pageable);
            } else {
                contactPage = contactRepository.findByUserIdAndGroupName(userId, group, pageable);
            }
        } else {
            // No filters, just search if provided
            if (search != null && !search.isBlank()) {
                contactPage = contactRepository.searchByUserIdAndName(userId, search.trim(), pageable);
            } else {
                contactPage = contactRepository.findByUserId(userId, pageable);
            }
        }

        return PagedResponseDTO.<ContactResponseDTO>builder()
            .content(contactPage.getContent().stream().map(mapper::toResponse).toList())
            .page(contactPage.getNumber())
            .size(contactPage.getSize())
            .totalElements(contactPage.getTotalElements())
            .totalPages(contactPage.getTotalPages())
            .last(contactPage.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public ContactResponseDTO getById(String principal, UUID id) {
        UUID userId = resolveUserId(principal);
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
        return mapper.toResponse(contact);
    }

    public ContactResponseDTO create(String principal, ContactRequestDTO req) {
        UUID userId = resolveUserId(principal);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Contact contact = Contact.builder().user(user).build();
        mapper.applyRequest(req, contact);
        return mapper.toResponse(contactRepository.save(contact));
    }

    public ContactResponseDTO update(String principal, UUID id, ContactRequestDTO req) {
        UUID userId = resolveUserId(principal);
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
        mapper.applyRequest(req, contact);
        return mapper.toResponse(contactRepository.save(contact));
    }

    public void delete(String principal, UUID id) {
        UUID userId = resolveUserId(principal);
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
        contactRepository.delete(contact);
    }

    public ContactResponseDTO toggleFavorite(String principal, UUID id) {
        UUID userId = resolveUserId(principal);
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
        contact.setIsFavorite(!Boolean.TRUE.equals(contact.getIsFavorite()));
        return mapper.toResponse(contactRepository.save(contact));
    }
}
