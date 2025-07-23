// File: UserRepository.java
package com.app.repository;

import com.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for User entity.
 * <p>
 * Provides CRUD operations and query methods for User data.
 * Extends JpaRepository to leverage standard Spring Data JPA functionality.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Default query methods provided by JpaRepository are sufficient.
}
