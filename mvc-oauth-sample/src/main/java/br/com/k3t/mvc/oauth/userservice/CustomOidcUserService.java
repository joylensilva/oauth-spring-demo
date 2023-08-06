package br.com.k3t.mvc.oauth.userservice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOidcUser loadedDefaultUser = (DefaultOidcUser) super.loadUser(userRequest);
        
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        JWT accessToken = null;
        try {
            accessToken = JWTParser.parse(tokenValue);
            @SuppressWarnings("unchecked")
            Map<String, Object> resourceAccessClaim = (Map<String, Object>) accessToken.getJWTClaimsSet().getClaim("resource_access");
            @SuppressWarnings("unchecked")
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccessClaim.get(userRequest.getClientRegistration().getClientId());

            if (clientRoles != null && !clientRoles.isEmpty()) {
                @SuppressWarnings("unchecked")
                ArrayList<String> roles = (ArrayList<String>) clientRoles.get("roles");
                
                if (roles != null && !roles.isEmpty()) {
                    Set<GrantedAuthority> authorities = new HashSet<>(loadedDefaultUser.getAuthorities());
                    
                    for(String role : roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                    
                    return getUser(userRequest, loadedDefaultUser.getUserInfo(), authorities);
                }
            }
        } catch (ParseException e) {
            // Do nothing
        }
                
        return loadedDefaultUser;
    }
    
    private OidcUser getUser(OidcUserRequest userRequest, OidcUserInfo userInfo, Set<GrantedAuthority> authorities) {
        ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        }
        return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }
    
}
