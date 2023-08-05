package br.com.k3t.mvc.oauth.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller for requests to the {@code /profile} resource. Populates the model with the claims from the
 * {@linkplain OidcUser} for use by the view.
 */
@Controller
public class ProfileController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        model.addAttribute("profile", oidcUser.getClaims());
        model.addAttribute("profileJson", claimsToJson(oidcUser.getClaims()));
        model.addAttribute("authoritiesJson", authoritiesToJson(oidcUser.getAuthorities()));
        return "profile";
    }

    private String authoritiesToJson(Collection<? extends GrantedAuthority> authorities) {
        try {
        	List<String> auths = authorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        	return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(auths);
        } catch (JsonProcessingException jpe) {
            log.error("Error parsing claims to JSON", jpe);
        }
        return "Error parsing claims to JSON.";
    }
    
    private String claimsToJson(Map<String, Object> claims) {
        try {
        	return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(claims);
        } catch (JsonProcessingException jpe) {
            log.error("Error parsing claims to JSON", jpe);
        }
        return "Error parsing claims to JSON.";
    }
}
