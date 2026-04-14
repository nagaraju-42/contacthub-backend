// backend/src/main/java/com/hub/dto/AuthResponseDTO.java
package com.hub.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponseDTO {
    private String token;
    private String message;
}
