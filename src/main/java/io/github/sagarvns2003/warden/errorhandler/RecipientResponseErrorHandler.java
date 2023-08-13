package io.github.sagarvns2003.warden.errorhandler;

import java.io.IOException;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

@Service
public class RecipientResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(RecipientResponseErrorHandler.class);

	@Override
	public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {
		if (clienthttpresponse.getStatusCode() != HttpStatus.OK) {
			logger.info("Status code: {}", clienthttpresponse.getStatusCode());
			logger.info("Response {}", clienthttpresponse.getStatusText());
			logger.info("{}", clienthttpresponse.getBody());

			if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
				logger.info("Call returned a error 403 forbidden resposne ");
				return true;
			}
		}
				
		return false;
	}

	@Override
	public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {
		logger.info("Status code: " + clienthttpresponse.getStatusCode());
		logger.info("Response" + clienthttpresponse.getStatusText());
		logger.info("{}", clienthttpresponse.getBody());
		if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
			logger.debug(HttpStatus.FORBIDDEN + " response. Throwing authentication exception");
			throw new RuntimeException("authentication"); // AuthenticationException();
		}
	}

}
