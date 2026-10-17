package com.mysociety.communication.tenant;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TenantContext {
    private static final String MEMBERSHIPS = "select distinct society_id from mysociety.user_society_roles where user_id = ? and is_active = true"
            + " and (valid_from is null or valid_from <= current_date) and (valid_until is null or valid_until >= current_date)";

    private final JdbcTemplate jdbc;
    private final boolean membershipFallback;

    public TenantContext(JdbcTemplate jdbc, @Value("${communication.tenant.membership-fallback:false}") boolean membershipFallback) {
        this.jdbc = jdbc;
        this.membershipFallback = membershipFallback;
    }

    public UUID societyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token))
            throw new AccessDeniedException("Authenticated tenant context is required");
        Object claim = ((Jwt) token.getPrincipal()).getClaims().get("society_id");
        if (claim != null) return parse(claim);
        if (!membershipFallback) throw new AccessDeniedException("Valid society_id claim is required");
        return fromMembership();
    }

    public UUID userId() {
        var authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getToken().getSubject());
    }

    // Used only when the token has no society_id claim: the society must be one the user actively belongs to.
    private UUID fromMembership() {
        List<UUID> societies = jdbc.queryForList(MEMBERSHIPS, UUID.class, userId());
        String requested = societyHeader();
        if (requested != null && !requested.isBlank()) {
            UUID id = parse(requested);
            if (!societies.contains(id)) throw new AccessDeniedException("User does not belong to the requested society");
            return id;
        }
        if (societies.size() == 1) return societies.get(0);
        throw new AccessDeniedException(societies.isEmpty() ? "User has no active society" : "x-society-id is required for users in multiple societies");
    }

    private UUID parse(Object value) {
        try {
            return UUID.fromString(String.valueOf(value));
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Valid society_id is required");
        }
    }

    private String societyHeader() {
        return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs
                ? attrs.getRequest().getHeader("x-society-id") : null;
    }
}
