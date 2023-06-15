/**
 * 
 */
package io.github.sagarvns2003.warden.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

/**
 * @author Vidya
 */
@Configuration
public class ChannelConfig {

	private ExecutorService executor = Executors.newCachedThreadPool();

	@Bean(name = "getSecretInboundChannel")
	public PublishSubscribeChannel getSecretInboundChannel() {
		return MessageChannels.publishSubscribe(executor).getObject();
	}

	@Bean(name = "replySecretChannel")
	public DirectChannel replySecretChannel() {
		return new DirectChannel();
	}

	/* -- Start: Multicasting channel configuration ---- */
	@Bean(name = "secretInboundPubSubChannel")
	public MessageChannel secretInboundPubSubChannel() {
		return new PublishSubscribeChannel();
	}

	@Bean(name = "secretInboundDirectChannel1")
	@BridgeFrom(value = "secretInboundPubSubChannel")
	public MessageChannel secretInboundDirectChannel1() {
		return new DirectChannel();
	}

	@Bean(name = "secretInboundDirectChannel2")
	@BridgeFrom(value = "secretInboundPubSubChannel")
	public MessageChannel secretInboundDirectChannel2() {
		return new DirectChannel();
	}
	/* -- End: Multicasting channel configuration ---- */

	@Bean(name = "secretOutboundChannel")
	public PublishSubscribeChannel secretOutboundChannel() {
		return MessageChannels.publishSubscribe(executor).getObject();
	}

	@Bean(name = "outboundHttpRecoveryChannel")
	public MessageChannel outboundHttpRecoveryChannel() {
	    return new DirectChannel();
	}
	
	@Bean(name = "expressionParser")
	public ExpressionParser expressionParser() {
		return new SpelExpressionParser();
	}
}