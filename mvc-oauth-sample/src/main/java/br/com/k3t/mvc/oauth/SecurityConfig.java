package br.com.k3t.mvc.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LogoutHandler logoutHandler;

    public SecurityConfig(LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                // Permite acesso total a home e as imagens
                customizer -> customizer.requestMatchers("/", "/images/**", "/css/**").permitAll()
                      // Todos os requests devem estar autenticados!
                      .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .logout(
                customizer -> customizer.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                        .addLogoutHandler(logoutHandler)
            );
        return http.build();
    }

// If using HS256, create a Bean to specify the HS256 should be used. By default, RS256 will be used.
//    @Bean
//    public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
//        OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
//        idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> MacAlgorithm.HS256);
//        return idTokenDecoderFactory;
//    }
}
