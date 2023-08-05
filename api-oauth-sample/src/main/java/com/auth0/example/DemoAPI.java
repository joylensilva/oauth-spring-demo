package com.auth0.example;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = DemoAPI.DEMO_API_TAG, description = "Projeto demonstração meetup OAuth 2.0 - Resource Server")
@RequestMapping(path = "api")
public interface DemoAPI {

	public static final String DEMO_API_TAG = "Demo API";
	
	@SecurityRequirements(value = {}) // Removendo necessidade de segurança no endpoint livre para todos.
	@Operation(summary = "Gera o ID único de uma pessoa", tags = DemoAPI.DEMO_API_TAG)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se o olá for dito com sucesso", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
	})
	@GetMapping
	ResponseEntity<MessageResponse> sayHelloToAll();
	
	@Operation(summary = "Gera o ID único de uma pessoa", tags = DemoAPI.DEMO_API_TAG)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se o olá for dito com sucesso para a pessoa autenticada", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
			,@ApiResponse(responseCode = "401", description = "Se o olá for negado pelo usuário não estar autenticado", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
			,@ApiResponse(responseCode = "403", description = "Se o olá for negado pelo usuário não ter permissão", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	})
	@GetMapping(path = "/protected")
	ResponseEntity<MessageResponse> sayProtectedHello(Authentication auth, Principal principal, @AuthenticationPrincipal Principal p2);
}
