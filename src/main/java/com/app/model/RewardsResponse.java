// src/main/java/com/app/model/dto/RewardsResponse.java
package com.app.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * DTO representing the response from Talon.One's reward evaluation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsResponse implements Serializable {
    private double discountAmount;
    private boolean loyaltyUsed;
    private List<String> appliedCampaigns;
    private List<String> appliedCoupons;
    private String message;
}
