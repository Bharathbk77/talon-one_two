package com.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * Configuration class for RestTemplate used to communicate with Talon.One's Integration API.
 * <p>
 * Provides a singleton, thread-safe RestTemplate bean configured with an interceptor
 * that attaches the Talon.One API key for authentication and logs essential request details.
 * </p>
 *
 * <pre>
 * Usage:
 *   - Inject RestTemplate where needed (e.g., TalonOneClient).
 *   - API key is securely injected from application properties.
 * </pre>
 */
@Configuration
public class RestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    /**
     * Talon.One API key, injected from application properties.
     */
    @Value("${talonone.api-key}")
    private String talonOneApiKey;

    /**
     * Defines a singleton RestTemplate bean configured for Talon.One Integration API.
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate talonOneRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(talonOneInterceptor()));
        return restTemplate;
    }

    /**
     * Creates a ClientHttpRequestInterceptor that:
     *   - Adds the Authorization header with the Talon.One API key.
     *   - Logs HTTP method and URI for each outgoing request.
     *   - Ensures sensitive information is not logged.
     *
     * @return configured ClientHttpRequestInterceptor
     */
    private ClientHttpRequestInterceptor talonOneInterceptor() {
        return new ClientHttpRequestInterceptor() {
            @Override
            public org.springframework.http.client.ClientHttpResponse intercept(
                    HttpRequest request,
                    byte[] body,
                    ClientHttpRequestExecution execution
            ) throws IOException {
                // Attach Authorization header using Bearer token
                HttpHeaders headers = request.getHeaders();
                headers.setBearerAuth(talonOneApiKey);

                // Log essential request details (method and URI only)
                logger.info("[Talon.One] Request: {} {}", request.getMethod(), request.getURI());

                return execution.execute(request, body);
            }
        };
    }
}
