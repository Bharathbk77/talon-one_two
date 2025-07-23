// src/main/java/com/app/model/dto/CartRequest.java
package com.app.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * DTO representing a cart for reward evaluation or order placement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest implements Serializable {
    private Long userId;
    private List<CartItemDTO> items;
    private double totalAmount;
    private ProfileDTO profileDTO;
    private SessionDTO sessionDTO;
}
