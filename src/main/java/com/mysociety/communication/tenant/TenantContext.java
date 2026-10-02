package com.mysociety.communication.tenant;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {
    public UUID societyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token))
            throw new AccessDeniedException("Authenticated tenant context is required");
        Object value = ((Jwt) token.getPrincipal()).getClaims().get("society_id");
        try {
            return UUID.fromString(String.valueOf(value));
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Valid society_id claim is required");
        }
    }

    public UUID userId() {
        var authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getToken().getSubject());
    }
}
