// src/main/java/com/app/model/dto/SessionDTO.java
package com.app.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO for session data sent to Talon.One for reward evaluation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO implements Serializable {
    private String integrationId; // maps to userId
    private List<CartItemDTO> cartItems;
    private double cartTotal;
    private Map<String, Object> sessionAttributes; // for extensibility
}
