package br.com.k3t.api.oauth.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApiImpl implements DemoAPI {
	
	@Override
	public ResponseEntity<MessageResponse> sayHelloToAll() {
		return ResponseEntity.ok(new MessageResponse("Olá todo mundo!"));
	}

	@Override
//	@Secured({"ROLE_Admin"})
	public ResponseEntity<MessageResponse> sayProtectedHello(@Autowired Authentication auth) {
	    String username = "Anonymous User";
	    if (JwtAuthenticationToken.class.isInstance(auth)) {
	        JwtAuthenticationToken authToken = (JwtAuthenticationToken) auth;
	        Jwt principal = (Jwt) authToken.getPrincipal();
	        String preferredUsername = principal.getClaimAsString("preferred_username");
	        if (preferredUsername != null) {
	            username = preferredUsername;
	        }
	    }
	    
	    return ResponseEntity.ok(new MessageResponse("Aqui deveria ser protegido! Bem vindo usuário '" + username + "'."));
	}

}
