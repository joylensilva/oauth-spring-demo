package com.auth0.example;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApiImpl implements DemoAPI {
	
	@Override
	public ResponseEntity<MessageResponse> sayHelloToAll() {
		return ResponseEntity.ok(new MessageResponse("Olá todo mundo!"));
	}

	@Override
	public ResponseEntity<MessageResponse> sayProtectedHello(Authentication auth, Principal principal, @AuthenticationPrincipal Principal p2) {
		return ResponseEntity.ok(new MessageResponse("Aqui deveria ser protegido! O usuário é: " + (auth != null ? auth.getName() : "No auth configured!") + "."));
	}

}
