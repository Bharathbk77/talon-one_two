package com.app.controller;

import com.app.model.OrderRequest;
import com.app.model.RewardsResponse;
import com.app.service.OrderService;
import com.app.service.RewardsService;
import com.app.service.UserService;
import jakarta.validation.Valid;
lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OrderController handles order placement and processing.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RewardsService rewardsService;
    private final UserService userService;

    /**
     * Place a new order, evaluate rewards, save order, and update user stats.
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Evaluate rewards
        RewardsResponse rewards = rewardsService.evaluateRewards(orderRequest.getCart());
        // Save order
        var savedOrder = orderService.saveOrder(orderRequest, rewards);
        // Update user stats
        userService.updateUserStatsAfterOrder(orderRequest.getUserId(), savedOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }
}
