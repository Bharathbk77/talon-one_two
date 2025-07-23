// File: OrderRepository.java
package com.app.repository;

import com.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Order entity.
 * <p>
 * Provides CRUD operations and query methods for Order data.
 * Extends JpaRepository to leverage standard Spring Data JPA functionality.
 * </p>
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Default query methods provided by JpaRepository are sufficient.
}
