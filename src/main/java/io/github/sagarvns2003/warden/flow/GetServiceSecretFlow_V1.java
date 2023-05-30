/**
 * 
 */
package io.github.sagarvns2003.warden.flow;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import io.github.sagarvns2003.warden.model.ServiceSecretPayload;
import io.github.sagarvns2003.warden.util.CommonUtil;
import io.github.sagarvns2003.warden.util.Constant;
import io.github.sagarvns2003.warden.util.JsonUtil;

/**
 * @author Vidya Sagar Gupta
 *
 */
@Component
public class GetServiceSecretFlow_V1 {

	private static final Logger logger = LoggerFactory.getLogger(GetServiceSecretFlow_V1.class);

	private static final String SERVICE_SECRET_API = Constant.API_V1 + "/secret/{serviceId}";
	private static final String[] REQUIRED_HEADERS = Constant.REQUIRED_HEADERS.stream().toArray(String[]::new);

	/*
	 * GET API
	 * 
	 */
	@Bean
	public HttpRequestHandlingMessagingGateway getSecretInboundHttp() {
		return Http.inboundGateway(SERVICE_SECRET_API)
				.requestMapping(r -> r.methods(HttpMethod.GET).produces(MediaType.APPLICATION_JSON_VALUE))
				.payloadExpression("#pathVariables.serviceId")
				.mappedRequestHeaders(REQUIRED_HEADERS)
				.requestChannel("getSecretInboundChannel")
				.replyChannel("replySecretChannel")
				.replyTimeout(20000)
				.getObject();
	}

	@Bean
	public IntegrationFlow secretInboundHttpRequestRouteToBridge() {
		return IntegrationFlow.from("getSecretInboundChannel")
				.log(LoggingHandler.Level.INFO,
						message -> MessageFormat.format("Get secrets for service id: {0}, request id: {1}",
								message.getPayload(),
								message.getHeaders().get(Constant.REQUEST_ID.toLowerCase(), String.class)))
				.transform(Message.class, message -> {
					String serviceId = (String) message.getPayload();
					String requestId = message.getHeaders().get(Constant.REQUEST_ID.toLowerCase(), String.class);
					String labels = message.getHeaders().get(Constant.SERVICE_LABELS.toLowerCase(), String.class);
					String recipientUrl = message.getHeaders().get(Constant.RECIPIENT_URL.toLowerCase(), String.class);
					return new ServiceSecretPayload(serviceId, requestId, CommonUtil.convertToList(labels),
							recipientUrl);
				})
				.channel("secretInboundPubSubChannel") // routing to bridge channel
				.get();
	}

	/*
	 * Ack/reply immediately
	 */
	@Bean
	public IntegrationFlow ackSecretInboundHttpRequest() {
		/*
		 * This channel secretInboundDirectChannel1 is connected to the bridge
		 * secretInboundPubSubChannel and will have same message
		 */
		return IntegrationFlow.from("secretInboundDirectChannel1")
				.transform(Message.class, message -> {
					ServiceSecretPayload serviceSecretPayload = (ServiceSecretPayload) message.getPayload();
					return Map.of("serviceId", serviceSecretPayload.serviceId(), "requestId", serviceSecretPayload.requestId(), "status", "IN_PROGRESS");
				})
				.channel("replySecretChannel")
				.get();
	}

	
	@ServiceActivator(inputChannel = "secretInboundDirectChannel2", requiresReply = "false")
	public void proceedForServiceSecret(Message<?> message) throws ExecutionException, InterruptedException {
		
		ServiceSecretPayload serviceSecretPayload = (ServiceSecretPayload) message.getPayload();
		Map<String, Object> httpHeaders = CommonUtil.extractAllHeaders(message.getHeaders());
		
		logger.info("serviceId: {}", serviceSecretPayload.serviceId());
		logger.info("labels: {}", serviceSecretPayload.labels());
		logger.info("all headers: {}", httpHeaders);

		/*
		if (personList.isEmpty()) {
			throw new ExecutionException("Empty person data for triggerId: " + triggerID, null);
		} else {
			// TODO: do transformation here
			// TODO: Can send to other channel like... otherChannel.send()
			System.out.println("Data... " + JsonUtil.toJsonString(personList));
		}*/
	}
	
	 /* 
	@Bean
	public IntegrationFlow proceedForServiceSecret() {
		return IntegrationFlow.from("secretInboundDirectChannel2")
				.log(LoggingHandler.Level.INFO, message -> MessageFormat.format("Proceed to get secrets for service id: {0}", ((ServiceSecretPayload)message.getPayload()).serviceId() ))
				.handle((payload, headers) -> {
					ServiceSecretPayload serviceSecretPayload = (ServiceSecretPayload) payload;
					
					//String serviceId = (String) payload;
					Map<String, Object> httpHeaders = CommonUtil.extractAllHeaders(headers);
					logger.info("serviceId: {}", serviceSecretPayload.serviceId());
					logger.info("headers: {}", httpHeaders);
					logger.info("labels: {}", serviceSecretPayload.labels()); //

					return "{}";
				}).channel("getSecretChannel").get();
	}*/
	 

}