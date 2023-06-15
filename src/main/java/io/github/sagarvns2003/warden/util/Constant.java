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

	String BLANK = "";
	
	// Http Headers
	String HEADER_X_AUTH_TOKEN = "X-AUTH-TOKEN";
	String HEADER_REQUEST_ID = "X-REQUEST-ID";
	String HEADER_SERVICE_LABELS = "X-SERVICE-LABELS";
	String HEADER_RECIPIENT_URL = "X-RECIPIENT-URL";
	List<String> REQUIRED_HEADERS = List.of(HEADER_X_AUTH_TOKEN, HEADER_REQUEST_ID, HEADER_SERVICE_LABELS,
			HEADER_RECIPIENT_URL);

}