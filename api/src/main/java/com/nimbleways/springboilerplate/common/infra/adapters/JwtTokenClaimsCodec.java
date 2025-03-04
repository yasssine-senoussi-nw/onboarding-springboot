package com.nimbleways.springboilerplate.common.infra.adapters;

import static java.lang.String.format;
import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;

import com.nimbleways.springboilerplate.common.domain.ports.RandomGeneratorPort;
import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.infra.mappers.RoleMapper;
import com.nimbleways.springboilerplate.common.infra.properties.JwtProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserPrincipal;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.AccessTokenDecodingException;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.TokenClaimsCodecPort;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.common.domain.ports.SecurityContextPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.*;
import javax.crypto.SecretKey;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenClaimsCodec implements TokenClaimsCodecPort, JwtDecoder, SecurityContextPort {
    private final JwtProperties jwtProperties;
    private final RandomGeneratorPort randomGenerator;
    private final TimeProviderPort timeProvider;
    private final SecretKey jwtSigningKey;

    public JwtTokenClaimsCodec(
        JwtProperties jwtProperties,
        RandomGeneratorPort randomGenerator,
        TimeProviderPort timeProvider
    ) {
        this.jwtProperties = jwtProperties;
        this.randomGenerator = randomGenerator;
        this.timeProvider = timeProvider;
        jwtSigningKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }

    @Override
    public AccessToken encode(TokenClaims tokenClaims) {
        UserPrincipal userPrincipal = tokenClaims.userPrincipal();
        Map<String, String> claims = Map.of(
                "scope", RoleMapper.INSTANCE.fromValueObject(userPrincipal.role()),
                "userId", userPrincipal.id().toString(),
                "email", userPrincipal.email().value()
        );

        return new AccessToken(
            Jwts.builder()
            .id(randomGenerator.uuid().toString().replace("-", ""))
            .subject(getSubject(userPrincipal))
            .issuer(jwtProperties.issuer())
            .issuedAt(Date.from(tokenClaims.creationTime()))
            .expiration(Date.from(tokenClaims.expirationTime()))
            .signWith(jwtSigningKey)
            .claims(claims)
            .compact());
    }

    @Override
    public TokenClaims decodeWithoutExpirationValidation(AccessToken token) {
        final Jwt jwt = getJwtWithoutExpirationValidation(token);
        return new TokenClaims(
            getUserPrincipal(jwt, token),
            // issuedAt and expiresAt are guaranteed to be not null
            castNonNull(jwt.getIssuedAt()),
            castNonNull(jwt.getExpiresAt())
        );
    }

    @Override
    public Jwt decode(String token) {
        try {
            return internalDecode(token);
        } catch (ExpiredJwtException ex) {
            throw getSpringException(ex);
        } catch (JwtException ex) {
            throw new BadJwtException("Cannot parse JWT", ex);
        }
    }

    private Jwt getJwtWithoutExpirationValidation(AccessToken token) {
        try {
            return internalDecode(token.value());
        } catch(ExpiredJwtException ex){
            return toJwt(token.value(), ex.getHeader(), ex.getClaims());
        } catch (JwtException ex) {
            throw new AccessTokenDecodingException(ex, token);
        }
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parser()
            .clock(() -> Date.from(timeProvider.instant()))
            .verifyWith(jwtSigningKey)
            .build()
            .parseSignedClaims(token);
    }

    @NotNull
    private static JwtValidationException getSpringException(ExpiredJwtException ex) {
        OAuth2Error expiredError = new OAuth2Error(
            "invalid_token",
            "Jwt expired at " + ex.getClaims().getExpiration().toInstant(),
            "https://tools.ietf.org/html/rfc6750#section-3.1"
        );
        return new JwtValidationException(
            "An error occurred while attempting to decode the Jwt: " + expiredError.getDescription(),
            List.of(expiredError)
        );
    }

    private static UserPrincipal getUserPrincipal(final Jwt jwt, AccessToken token) {
        String[] subjectFields = jwt.getSubject().split(",");
        Role role = getRole(jwt, token);
        return new UserPrincipal(
            UUID.fromString(subjectFields[0]),
            new Email(subjectFields[1]),
            role
        );
    }

    private static String getSubject(final UserPrincipal userPrincipal) {
        return format("%s,%s", userPrincipal.id(), userPrincipal.email().value());
    }

    private static Role getRole(Jwt jwt, AccessToken token) {
        Object roleClaim = jwt.getClaim("scope");
        if (roleClaim == null) {
            throw new AccessTokenDecodingException("missing claim 'scope'", token);
        }
        if (roleClaim instanceof String role) {
            return RoleMapper.INSTANCE.toValueObject(role);
        }
        throw new AccessTokenDecodingException(
            "claim 'scope' is not a string: " + roleClaim, token);
    }

    private Jwt internalDecode(String token) {
        Jws<Claims> headerClaimsJwt = getClaimsJws(token);
        return toJwt(token, headerClaimsJwt.getHeader(), headerClaimsJwt.getPayload());
    }

    @NotNull
    private static Jwt toJwt(String token, Map<String, Object> headers, Claims payload) {
        return Jwt
            .withTokenValue(token)
            .headers(map -> map.putAll(headers))
            .claims(claims -> claims.putAll(payload))
            .issuedAt(payload.getIssuedAt().toInstant())
            .notBefore(payload.getIssuedAt().toInstant())
            .expiresAt(payload.getExpiration().toInstant())
            .build();
    }

    @Override
    public Optional<UUID> getCurrentUserId() {
        return getCurrentClaim()
                .map(jwt -> (String) jwt.getClaim("userId"))
                .map(UUID::fromString);
    }

    @Override
    public Optional<Email> getCurrentUserEmail() {
        return getCurrentClaim()
                .map(jwt -> new Email(jwt.getClaim("email")));
    }

    private Optional<Jwt> getCurrentClaim() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }

        if (!(auth.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        return Optional.of(jwt);
    }
}
