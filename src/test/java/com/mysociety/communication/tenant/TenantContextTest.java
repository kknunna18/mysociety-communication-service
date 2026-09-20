package com.mysociety.communication.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class TenantContextTest {
    private final TenantContext context = new TenantContext();
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void readsTenantOnlyFromVerifiedJwtClaim() {
        UUID society = UUID.randomUUID(); UUID user = UUID.randomUUID();
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg","HS256"), Map.of("sub",user.toString(),"society_id",society.toString()));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        assertThat(context.societyId()).isEqualTo(society); assertThat(context.userId()).isEqualTo(user);
    }
    @Test void rejectsMissingTenantClaim() {
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg","HS256"), Map.of("sub",UUID.randomUUID().toString()));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        assertThatThrownBy(context::societyId).isInstanceOf(AccessDeniedException.class);
    }
}
