package com.auth0.example;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
		info = @Info(
				title = "OAuth2 Demo"
				,description = "Projeto demonstração OAuth 2.0 e OpenID Connect 1.0"
				,version = "1.0.0"
				,contact = @Contact(
							name = "Joyle Novaes da Silva"
							,email = "joyle@kode3.tech"
							,url = "https://github.com/joylensilva"
						)
				,license = @License(
							name = "Apache License Version 2.0"
							,url = ""
						)
				)
		,security = { @SecurityRequirement(name = "OAuth2Security") }
		)
@SecurityScheme(
		name = "OAuth2Security", 
		type = SecuritySchemeType.OAUTH2,
		flows = @OAuthFlows(authorizationCode = 
			@OAuthFlow(
					authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}", 
					tokenUrl = "${springdoc.oAuthFlow.tokenUrl}", 
					refreshUrl = "${springdoc.oAuthFlow.tokenUrl}"
					)))
@Configuration
public class OpenAPIConfig {

}
