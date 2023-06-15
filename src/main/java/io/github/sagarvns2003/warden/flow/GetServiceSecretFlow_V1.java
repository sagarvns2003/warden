/**
 * 
 */
package io.github.sagarvns2003.warden.flow;

import java.text.MessageFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.ExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.support.MessageBuilder;
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

	
	
	@Autowired
	private ExpressionParser expressionParser;
	
	@Autowired
	private RequestHandlerRetryAdvice outboundHttpRequestHandlerRetryAdvice;
	//@Autowired
	//private ReceipentResponseErrorHandler receipentResponseErrorHandler;
	
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
				//.statusCodeExpression(HttpStatus.ACCEPTED)
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
								message.getHeaders().get(Constant.HEADER_REQUEST_ID.toLowerCase(), String.class)))
				.transform(Message.class, message -> {
					String serviceId = (String) message.getPayload();
					String requestId = message.getHeaders().get(Constant.HEADER_REQUEST_ID.toLowerCase(), String.class);
					String labels = message.getHeaders().get(Constant.HEADER_SERVICE_LABELS.toLowerCase(), String.class);
					String recipientUrl = message.getHeaders().get(Constant.HEADER_RECIPIENT_URL.toLowerCase(), String.class);
					return new ServiceSecretPayload(serviceId, requestId, CommonUtil.convertToMap(labels), recipientUrl);
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

	
	@ServiceActivator(inputChannel = "secretInboundDirectChannel2", outputChannel = "secretOutboundChannel")
	public Message<?> proceedForServiceSecret(Message<?> message) {
		
		Map<String, Object> httpHeaders = CommonUtil.extractAllHeaders(message.getHeaders());
		ServiceSecretPayload serviceSecretPayload = (ServiceSecretPayload) message.getPayload();
		
		Map<String, String> labelMap = serviceSecretPayload.labels();

		logger.info("all headers: {}", httpHeaders);
		logger.info("serviceId: {}", serviceSecretPayload.serviceId());
		//logger.info("labels: {}", serviceSecretPayload.labels());

		//TODO: Get the passwords and populate labelMap
		//labelMap.entrySet().stream().
		
		//send to outBoundChannel
		return MessageBuilder.withPayload(JsonUtil.toJsonString(serviceSecretPayload.labels())).copyHeaders(httpHeaders).build();
	}
	
	
	@Bean
	public IntegrationFlow secretOutboundHttp() {
		return IntegrationFlow.from("secretOutboundChannel")
				.log(LoggingHandler.Level.INFO,
						message -> MessageFormat.format("Proceed to send the secrets to the recipient url {0}",
								message.getHeaders().get(Constant.HEADER_RECIPIENT_URL.toLowerCase(), String.class)))
				//.log(LoggingHandler.Level.INFO,	message -> MessageFormat.format("Secret payload {0}", (String) message.getPayload()))
				.handle(Http.outboundGateway(expressionParser.parseExpression("headers['x-recipient-url']"))
						//.outboundGateway("http://localhost:8080/v1/api/job/recieve")
						.httpMethod(HttpMethod.POST)
						.expectedResponseType(String.class)
						//.errorHandler(this.receipentResponseErrorHandler)
						.extractPayload(true)
						.extractResponseBody(true), e -> e.advice(this.outboundHttpRequestHandlerRetryAdvice)
						//.getObject()
						)
				.log(LoggingHandler.Level.INFO,
						message -> MessageFormat.format("Response from the recipient url {0} is... {1}",
								message.getHeaders().get(Constant.HEADER_RECIPIENT_URL.toLowerCase(), String.class),
								(String) message.getPayload()))
				.transform(Message.class, message -> {
					logger.info("response: {}", message.getPayload());
					return "";
				})
				.get();
				//.channel("getSecretChannel").get();
	}
	
	@Bean
	public IntegrationFlow handleRecovery() {
		return IntegrationFlow.from("outboundHttpRecoveryChannel")
				.log(LoggingHandler.Level.ERROR, "error: ", m -> m.getPayload())
				.transform(Message.class, message -> {
					logger.info("outboundHttpRecoveryChannel: {}", message.getPayload());
					return "";
				})
				.get();
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