package io.github.sagarvns2003.warden.config;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Autowired
	private ServerProperties serverProperties;

	@Autowired
	private BuildProperties buildProperties;

	public String appName() {
		return this.buildProperties.getName();  //this.buildProperties.getArtifact();
	}
	
	public String appVersion() {
		return this.buildProperties.getVersion();
	}
	
	public Instant appBuildTime() {
		return this.buildProperties.getTime();
	}
	
	public String appJvmVersion() {
		return this.buildProperties.get("java.version");
	}
	
	public int port() {
		return this.serverProperties.getPort();
	}

	public String appUrl() {
		return "http://localhost:" + this.port();
	}

	/*
	 * public String swaggerUrl() { return "http://localhost:" + this.port() +
	 * "/swagger-ui/index.html"; }
	 */

	
	
}
