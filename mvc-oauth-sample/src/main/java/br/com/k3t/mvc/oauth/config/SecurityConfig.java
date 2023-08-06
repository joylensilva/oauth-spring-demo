package br.com.k3t.mvc.oauth.config;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.k3t.mvc.oauth.controllers.LogoutHandler;
import br.com.k3t.mvc.oauth.userservice.CustomOidcUserService;

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
//            .oauth2Login(
//                        oauth2LoginConfigurer -> oauth2LoginConfigurer.userInfoEndpoint(
//                                userinfoEndpointConfig -> userinfoEndpointConfig.oidcUserService(new CustomOidcUserService()))
//                        )
//            .oauth2Login(
//                          oauth2LoginConfigurer -> oauth2LoginConfigurer.userInfoEndpoint(
//                                  userinfoEndpointConfig -> userinfoEndpointConfig.userAuthoritiesMapper(userAuthoritiesMapper())
//                          )
//                    )
            .logout(
                customizer -> customizer.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                        .addLogoutHandler(logoutHandler)
            );
        return http.build();
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return new CustomOidcUserService();
    }
    
// https://docs.spring.io/spring-security/site/docs/5.0.7.RELEASE/reference/html/oauth2login-advanced.html#oauth2login-advanced-map-authorities-grantedauthoritiesmapper
//    @Bean
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(authorities);

            authorities.forEach(authority -> {
                if (OidcUserAuthority.class.isInstance(authority)) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    
                    Object resourceAccessClaim = oidcUserAuthority.getAttributes().get("resource_access");
                    if (resourceAccessClaim != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> rac = (Map<String, Object>) resourceAccessClaim;
                        
                        Object clientRolesClaim = rac.get("demo-mvc-client");
                        if (clientRolesClaim != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> crc = (Map<String, Object>) clientRolesClaim;
                            
                            Object rolesClaim = crc.get("roles");
                            if (rolesClaim != null) {
                                @SuppressWarnings("unchecked")
                                List<String> roles = (List<String>) rolesClaim;
                                
                                for (String role : roles) {
                                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                                }
                            }
                        }
                        
                    }
                }
            });

            return mappedAuthorities;
        };
    }
    
// If using HS256, create a Bean to specify the HS256 should be used. By default, RS256 will be used.
//    @Bean
//    public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
//        OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
//        idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> MacAlgorithm.HS256);
//        return idTokenDecoderFactory;
//    }
}
