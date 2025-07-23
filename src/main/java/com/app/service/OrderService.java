// src/main/java/com/app/service/OrderService.java
package com.app.service;

import com.app.model.Order;
import com.app.model.OrderRequest;
import com.app.model.CartRequest;
import com.app.model.RewardsResponse;
import com.app.repository.OrderRepository;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer for order processing and business logic.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final RewardsService rewardsService;
    private final OrderRepository orderRepository;

    /**
     * Saves a new order after evaluating rewards and applying discounts.
     * @param orderRequest The order request data.
     * @param rewards The evaluated rewards to apply.
     * @return The saved Order object.
     */
    public Order saveOrder(OrderRequest orderRequest, RewardsResponse rewards) {
        // Retrieve user and cart details
        var user = userService.getUserById(orderRequest.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found for order placement.");
        }

        // Calculate total with discount
        double originalTotal = orderRequest.getCart().getTotalAmount();
        double discount = rewards != null ? rewards.getDiscountAmount() : 0.0;
        double finalTotal = originalTotal - discount;

        // Create Order entity
        Order order = new Order();
        order.setUserId(user.getId());
        order.setItems(orderRequest.getCart().getItems());
        order.setTotalAmount(finalTotal);
        order.setDiscountApplied(discount);
        order.setRewardDetails(rewards);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Confirm loyalty point usage if applicable
        if (rewards != null && rewards.isLoyaltyUsed()) {
            rewardsService.confirmLoyalty(user.getId().toString(), finalTotal);
        }

        return savedOrder;
    }

    /**
     * Places an order: evaluates rewards, saves the order, updates user stats, and confirms loyalty.
     * @param req The order request.
     * @return The saved Order object.
     */
    public Order placeOrder(OrderRequest req) {
        // Retrieve user
        var user = userService.getUserById(req.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found for order placement.");
        }

        // Evaluate rewards/discounts
        RewardsResponse rewards = rewardsService.evaluateRewards(req.getCart());

        // Save order with applied rewards
        Order savedOrder = saveOrder(req, rewards);

        // Update user statistics
        userService.updateUserStatsAfterOrder(req.getUserId(), savedOrder);

        // Confirm loyalty point usage if applicable
        if (rewards != null && rewards.isLoyaltyUsed()) {
            rewardsService.confirmLoyalty(user.getId().toString(), savedOrder.getTotalAmount());
        }

        return savedOrder;
    }
}
