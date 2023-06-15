/**
 * 
 */
package io.github.sagarvns2003.warden.model;

import java.util.Map;

/**
 * @author Vidya Sagar Gupta
 *
 */
public record ServiceSecretPayload(String serviceId, String requestId, Map<String, String> labels, String recipientUrl) {
}