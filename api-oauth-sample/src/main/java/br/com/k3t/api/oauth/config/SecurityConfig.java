package br.com.k3t.api.oauth.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	public static final String[] SWAGGER_URIS = {
			"/", // Raiz redireciona para a doc do swagger.
			"/swagger-ui/**",
			"/v3/api-docs/**"
	};
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(c -> c.requestMatchers(SWAGGER_URIS).permitAll()
                                         .requestMatchers("/api").permitAll()
                                         .anyRequest().authenticated()
                                         );
        
        http.cors(c -> c.disable());        
        http.oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.jwtAuthenticationConverter(converter())));
        return http.build();
    }
    
    public Converter<Jwt, ? extends AbstractAuthenticationToken> converter() {
        return jwt -> {
            return new JwtAuthenticationToken(jwt, extractAuthorities(jwt));
        };
    }
    
    private Collection<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        @SuppressWarnings("unchecked")
        Map<String, Object> ra = (Map<String, Object>) claims.get("resource_access");

        if (ra != null && !ra.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> clientResources = (Map<String, Object>) ra.get("demo-mvc-client");

            if (clientResources != null && !clientResources.isEmpty()) {
                @SuppressWarnings("unchecked")
                List<Object> roles = (List<Object>) clientResources.get("roles");

                if (roles != null && !roles.isEmpty()) {
                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString())).toList();
                }
            }
        }
        
        return Collections.emptyList();
    }
    
}
