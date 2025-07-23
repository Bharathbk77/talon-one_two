package com.app.talonone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import com.app.model.dto.ProfileDTO;
import com.app.model.dto.SessionDTO;
import com.app.model.dto.RewardsResponse;

/**
 * TalonOneClient is a reusable, Spring-managed client for interacting with Talon.One's Integration API.
 * <p>
 * It provides methods to update customer profiles, evaluate sessions for rewards/discounts,
 * and confirm loyalty transactions. Configuration is loaded from application.properties.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     talonOneClient.updateProfile(userId, profileDTO);
 *     RewardsResponse rewards = talonOneClient.evaluateSession(sessionDTO);
 *     talonOneClient.confirmLoyalty(userId, totalAmount);
 * </pre>
 * </p>
 */
@Component
public class TalonOneClient {

    private static final Logger logger = LoggerFactory.getLogger(TalonOneClient.class);

    @Value("${talonone.base-url}")
    private String baseUrl;

    @Value("${talonone.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    /**
     * Constructs the TalonOneClient with a provided RestTemplate.
     * @param restTemplate the RestTemplate to use for HTTP communication
     */
    public TalonOneClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Updates a customer profile in Talon.One.
     *
     * @param userId the user ID to update
     * @param dto the profile data
     * @throws TalonOneClientException if the request fails
     */
    public void updateProfile(String userId, ProfileDTO dto) {
        String url = String.format("%s/v1/profiles/%s", trimBaseUrl(), encode(userId));
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProfileDTO> request = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to update profile for userId {}: HTTP {}", userId, response.getStatusCode());
                throw new TalonOneClientException("Failed to update profile: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("Talon.One profile update failed for userId {}: {} - {}", userId, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new TalonOneClientException("Talon.One profile update failed: " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            logger.error("Talon.One profile update failed for userId {}: {}", userId, ex.getMessage());
            throw new TalonOneClientException("Talon.One profile update failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Evaluates a session in Talon.One to determine rewards/discounts.
     *
     * @param dto the session data
     * @return the evaluated rewards response
     * @throws TalonOneClientException if the request fails
     */
    public RewardsResponse evaluateSession(SessionDTO dto) {
        String url = String.format("%s/v1/sessions", trimBaseUrl());
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SessionDTO> request = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<RewardsResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, RewardsResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                logger.error("Failed to evaluate session: HTTP {}", response.getStatusCode());
                throw new TalonOneClientException("Failed to evaluate session: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("Talon.One session evaluation failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new TalonOneClientException("Talon.One session evaluation failed: " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            logger.error("Talon.One session evaluation failed: {}", ex.getMessage());
            throw new TalonOneClientException("Talon.One session evaluation failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Confirms a loyalty transaction for a user in Talon.One.
     *
     * @param userId the user ID
     * @param totalAmount the total amount for the loyalty transaction
     * @throws TalonOneClientException if the request fails
     */
    public void confirmLoyalty(String userId, double totalAmount) {
        String url = String.format("%s/v1/loyalty/%s/confirm", trimBaseUrl(), encode(userId));
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"totalAmount\": %.2f}", totalAmount);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to confirm loyalty for userId {}: HTTP {}", userId, response.getStatusCode());
                throw new TalonOneClientException("Failed to confirm loyalty: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("Talon.One loyalty confirmation failed for userId {}: {} - {}", userId, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new TalonOneClientException("Talon.One loyalty confirmation failed: " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            logger.error("Talon.One loyalty confirmation failed for userId {}: {}", userId, ex.getMessage());
            throw new TalonOneClientException("Talon.One loyalty confirmation failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Creates HTTP headers with Authorization for Talon.One API requests.
     *
     * @return HttpHeaders with Bearer token and Accept: application/json
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(java.util.Collections.singletonList(StandardCharsets.UTF_8));
        return headers;
    }

    /**
     * Trims the trailing slash from the base URL if present.
     * @return the trimmed base URL
     */
    private String trimBaseUrl() {
        return baseUrl != null && baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * URL-encodes a string for safe use in path variables.
     *
     * @param value the string to encode
     * @return the encoded string
     */
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            logger.warn("Failed to encode value '{}': {}", value, e.getMessage());
            return value;
        }
    }

    /**
     * Exception thrown for TalonOneClient errors.
     */
    public static class TalonOneClientException extends RuntimeException {
        public TalonOneClientException(String message) {
            super(message);
        }
        public TalonOneClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
