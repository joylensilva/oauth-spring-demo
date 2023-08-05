package com.auth0.example;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

//	public static final String[] SWAGGER_URIS = {
//			"/", // Raiz redireciona para a doc do swagger.
//			"/swagger-ui/**",
//			"/v3/api-docs/**"
//	};
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.anyRequest()
        	.permitAll();
        
        http.cors().disable(); // Atenção aqui!
    	
//    	http
//    		.authorizeRequests()
//    		.antMatchers(SWAGGER_URIS).permitAll()
//    		.antMatchers(HttpMethod.GET, "/api").permitAll()
//    		.antMatchers(HttpMethod.GET, "/api/protected").authenticated()
////    		.antMatchers(HttpMethod.GET, "/api/protected").hasRole("read:protected")
//			.anyRequest().authenticated()
//		.and()
//			.oauth2ResourceServer()
//			.jwt()
//			.jwtAuthenticationConverter(converter())
//			;
    	
        return http.build();
    }
    
//    @Bean
//    public JwtAuthenticationConverter converter() {
//    	JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//    	
//    	JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
//    	authoritiesConverter.setAuthoritiesClaimName("permissions");
//    	// O default é SCOPE_!!! Aqui é possível especializar o converter para cada situação!
//    	authoritiesConverter.setAuthorityPrefix("ROLE_");
//    	
//    	converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
//    	
//    	return converter;
//    }
    
}
