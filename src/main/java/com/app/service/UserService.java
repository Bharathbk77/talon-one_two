// src/main/java/com/app/service/UserService.java
package com.app.service;

import com.app.model.User;
import com.app.model.Order;
import com.app.repository.UserRepository;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for user-related business logic.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Fetches a user by their ID.
     * @param id The ID of the user.
     * @return The User object if found, otherwise null.
     */
    public User getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.orElse(null);
    }

    /**
     * Updates a user's totalOrders and totalSpent statistics.
     * @param id The ID of the user to update.
     * @param updateRequest The User object containing updated stats.
     * @return The updated User object, or null if not found.
     */
    public User updateUserStats(Long id, User updateRequest) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }
        User user = userOpt.get();
        user.setTotalOrders(updateRequest.getTotalOrders());
        user.setTotalSpent(updateRequest.getTotalSpent());
        return userRepository.save(user);
    }

    /**
     * Updates user statistics after a new order is placed.
     * @param userId The ID of the user.
     * @param order The order that was placed.
     */
    public void updateUserStatsAfterOrder(Long userId, Order order) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && order != null) {
            User user = userOpt.get();
            user.setTotalOrders(user.getTotalOrders() + 1);
            user.setTotalSpent(user.getTotalSpent() + order.getTotalAmount());
            userRepository.save(user);
        }
    }
}
