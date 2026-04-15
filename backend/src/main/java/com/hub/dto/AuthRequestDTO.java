// backend/src/main/java/com/hub/dto/AuthRequestDTO.java
package com.hub.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthRequestDTO {
    private String name;     // For registration
    private String email;
    private String password;
}
