// src/main/java/com/app/model/dto/OrderRequest.java
package com.app.model;

import com.app.model.CartRequest;
lombok.*;

import java.io.Serializable;

/**
 * DTO for placing an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest implements Serializable {
    private Long userId;
    private CartRequest cart;
}
