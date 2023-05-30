/**
 * 
 */
package io.github.sagarvns2003.warden.util;

import java.util.List;

/**
 * @author Vidya Sagar Gupta
 *
 */
public interface Constant {

	String API_V1 = "/api/v1";
	// String API_V2 = "/api/v2";

	//Http Headers
	String REQUEST_ID = "X-REQUEST-ID";
	String SERVICE_LABELS = "X-SERVICE-LABELS";
	String RECIPIENT_URL = "X-RECIPIENT-URL";
	List<String> REQUIRED_HEADERS = List.of("X-AUTH-TOKEN", REQUEST_ID, SERVICE_LABELS, RECIPIENT_URL);

}