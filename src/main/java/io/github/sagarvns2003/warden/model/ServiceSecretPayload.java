/**
 * 
 */
package io.github.sagarvns2003.warden.model;

import java.util.List;

/**
 * @author Vidya Sagar Gupta
 *
 */
public record ServiceSecretPayload(String serviceId, String requestId, List<String> labels, String recipientUrl) {
}