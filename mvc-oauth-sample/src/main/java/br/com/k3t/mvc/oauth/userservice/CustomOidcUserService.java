package br.com.k3t.mvc.oauth.userservice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {//extends OidcUserService {

    final OidcUserService delegate = new OidcUserService();
    
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser loadedDefaultUser = delegate.loadUser(userRequest);
        
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        String clientId = userRequest.getClientRegistration().getClientId();
        
        Set<GrantedAuthority> authorities = fetchAuthoritiesFromAccessToken(tokenValue, loadedDefaultUser, clientId);
//        Set<GrantedAuthority> authorities = fetchAuthoritiesFromUserInfo(loadedDefaultUser, clientId);
        
        return getUser(userRequest, loadedDefaultUser.getUserInfo(), authorities);
    }
    
    private Set<GrantedAuthority> fetchAuthoritiesFromUserInfo(OidcUser oidcUser, String clientId) {
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        
        Object resourceAccessClaim = oidcUser.getUserInfo().getClaim("resource_access");
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
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }
            
        }
        
        return authorities;
    }
    
    private Set<GrantedAuthority> fetchAuthoritiesFromAccessToken(String accessTokenValue, OidcUser oidcUser, String clientId) {
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        
        JWT accessToken = null;
        try {
            accessToken = JWTParser.parse(accessTokenValue);
            @SuppressWarnings("unchecked")
            Map<String, Object> resourceAccessClaim = (Map<String, Object>) accessToken.getJWTClaimsSet().getClaim("resource_access");
            @SuppressWarnings("unchecked")
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccessClaim.get(clientId);

            if (clientRoles != null && !clientRoles.isEmpty()) {
                @SuppressWarnings("unchecked")
                ArrayList<String> roles = (ArrayList<String>) clientRoles.get("roles");
                
                if (roles != null && !roles.isEmpty()) {
                    
                    for(String role : roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                    
                    return authorities;
                }
            }
        } catch (ParseException e) {
            // Do nothing
        }
        
        return authorities;
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
