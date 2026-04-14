// backend/src/main/java/com/hub/controller/ContactController.java
package com.hub.controller;

import com.hub.dto.AvatarUploadResponseDTO;
import com.hub.dto.ContactRequestDTO;
import com.hub.dto.ContactResponseDTO;
import com.hub.dto.PagedResponseDTO;
import com.hub.service.CloudinaryService;
import com.hub.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ContactResponseDTO>> getAll(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) Boolean favorite) {
        return ResponseEntity.ok(
            contactService.getAll(auth.getName(), page, size, search, group, favorite)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getById(
            Authentication auth,
            @PathVariable UUID id) {
        return ResponseEntity.ok(contactService.getById(auth.getName(), id));
    }

    @PostMapping
    public ResponseEntity<ContactResponseDTO> create(
            Authentication auth,
            @Valid @RequestBody ContactRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(contactService.create(auth.getName(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> update(
            Authentication auth,
            @PathVariable UUID id,
            @Valid @RequestBody ContactRequestDTO req) {
        return ResponseEntity.ok(contactService.update(auth.getName(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication auth,
            @PathVariable UUID id) {
        contactService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ContactResponseDTO> toggleFavorite(
            Authentication auth,
            @PathVariable UUID id) {
        return ResponseEntity.ok(contactService.toggleFavorite(auth.getName(), id));
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarUploadResponseDTO> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.upload(file);
        return ResponseEntity.ok(AvatarUploadResponseDTO.builder().url(url).build());
    }
}
