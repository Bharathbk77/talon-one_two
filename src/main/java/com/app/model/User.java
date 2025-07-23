// src/main/java/com/app/model/User.java
package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * User entity representing an application user.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private int totalOrders;

    private double totalSpent;

    private int loyaltyPoints;

    // One user can have multiple orders
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders;
}
