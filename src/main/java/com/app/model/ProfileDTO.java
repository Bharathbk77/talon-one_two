// src/main/java/com/app/model/dto/ProfileDTO.java
package com.app.model;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for user profile data sent to Talon.One.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO implements Serializable {
    private String integrationId; // maps to userId
    private String email;
    private String name;
    private int loyaltyPoints;
    private Map<String, Object> attributes; // for extensibility
}
