/**
 * 
 */
package io.github.sagarvns2003.warden.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author Vidya Sagar Gupta
 *
 */
@Configuration
public class RetryConfig {

	@Autowired
	private MessageChannel outboundHttpRecoveryChannel;

	@Value("${retry.maxAttempts:3}")
	private int retryMaxAttempts;

	@Value("${retry.maxDelay:500}")
	private long retryMaxDelay;

	@Bean(name = "outboundHttpRequestHandlerRetryAdvice")
	public RequestHandlerRetryAdvice outboundHttpRequestHandlerRetryAdvice() {
		RequestHandlerRetryAdvice requestHandlerRetryAdvice = new RequestHandlerRetryAdvice();
		requestHandlerRetryAdvice.setRetryTemplate(this.retryTemplate());
		requestHandlerRetryAdvice.setRecoveryCallback(new ErrorMessageSendingRecoverer(outboundHttpRecoveryChannel));
		return requestHandlerRetryAdvice;
	}

	private RetryTemplate retryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(this.retryPolicy());
		retryTemplate.setBackOffPolicy(this.fixedBackOffPolicy());
		return retryTemplate;
	}

	// @Bean
	private SimpleRetryPolicy retryPolicy() {
		
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(retryMaxAttempts, Map.of(RuntimeException.class, true));
		retryPolicy.setMaxAttempts(this.retryMaxAttempts);
		return retryPolicy;
	}

	// @Bean
	private FixedBackOffPolicy fixedBackOffPolicy() {
		FixedBackOffPolicy p = new FixedBackOffPolicy();
		p.setBackOffPeriod(this.retryMaxDelay);
		return p;
	}

}
