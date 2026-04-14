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

    // Resolve user — in production, derive userId from JWT principal.
    // For local testing, we use a fixed mock UUID or look up by mock user ID.
    private static final UUID MOCK_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private UUID resolveUserId(String principal) {
        // principal is "mock-user-id" string set by JwtFilter
        return MOCK_USER_ID;
    }

    private User getOrCreateMockUser() {
        return userRepository.findById(MOCK_USER_ID).orElseGet(() -> {
            User user = User.builder()
                .id(MOCK_USER_ID)
                .name("Mock User")
                .email("mock@contacthub.dev")
                .passwordHash("$2a$10$placeholder")
                .build();
            return userRepository.save(user);
        });
    }

    @Transactional(readOnly = true)
    public PagedResponseDTO<ContactResponseDTO> getAll(String principal,
                                                        int page, int size,
                                                        String search) {
        UUID userId = resolveUserId(principal);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contactPage;

        if (search != null && !search.isBlank()) {
            contactPage = contactRepository.searchByUserIdAndName(userId, search.trim(), pageable);
        } else {
            contactPage = contactRepository.findByUserId(userId, pageable);
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
        User user = getOrCreateMockUser();
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
