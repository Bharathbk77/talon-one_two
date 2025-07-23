// src/main/java/com/app/service/RewardsService.java
package com.app.service;

import com.app.model.CartRequest;
import com.app.model.RewardsResponse;
import com.app.talonone.TalonOneClient;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing rewards and discounts via Talon.One API.
 */
@Service
@RequiredArgsConstructor
public class RewardsService {

    private final TalonOneClient talonOneClient;

    /**
     * Evaluates rewards and discounts for a given cart by interacting with Talon.One.
     * @param cartRequest The cart request containing items and user info.
     * @return The evaluated RewardsResponse.
     */
    public RewardsResponse evaluateRewards(CartRequest cartRequest) {
        // Update user profile in Talon.One
        talonOneClient.updateProfile(cartRequest.getUserId(), cartRequest.getProfileDTO());

        // Evaluate the cart/session in Talon.One
        RewardsResponse response = talonOneClient.evaluateSession(cartRequest.getUserId(), cartRequest.getSessionDTO());

        return response;
    }

    /**
     * Confirms loyalty point usage for a user and order total via Talon.One.
     * @param userId The ID of the user.
     * @param total The total amount for which loyalty is being confirmed.
     */
    public void confirmLoyalty(String userId, double total) {
        talonOneClient.confirmLoyalty(userId, total);
    }
}
