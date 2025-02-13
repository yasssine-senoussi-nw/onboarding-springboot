package com.nimbleways.springboilerplate.features.authentication.domain.ports;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.UserPrincipalFixture.aUserPrincipal;
import static org.junit.jupiter.api.Assertions.*;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.AccessTokenDecodingException;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class TokenClaimsCodecPortContractTests {
    private TokenClaimsCodecPort tokenCodec;

    @BeforeEach
    public void createSut() {
        tokenCodec = getInstance();
    }

    @Test
    void encoding_a_valid_claim_returns_a_non_null_accesstoken() {
        TokenClaims claims = getTokenClaims();

        AccessToken token = tokenCodec.encode(claims);

        assertNotNull(token);
    }

    @Test
    void encoding_the_same_claim_twice_returns_different_accesstokens() {
        TokenClaims claims = getTokenClaims();

        AccessToken firstToken = tokenCodec.encode(claims);
        AccessToken secondToken = tokenCodec.encode(claims);

        assertNotEquals(firstToken, secondToken);
    }

    @Test
    void decoding_a_valid_accesstoken_returns_the_initial_claim() {
        TokenClaims claims = getTokenClaims();
        AccessToken token = tokenCodec.encode(claims);

        TokenClaims decodedClaims = tokenCodec.decodeWithoutExpirationValidation(token);

        assertEquals(claims, decodedClaims);
    }

    @Test
    void decoding_an_expired_accesstoken_returns_the_initial_claim() {
        Instant epochZero = Instant.ofEpochMilli(0);
        TokenClaims claims = new TokenClaims(aUserPrincipal().build(), epochZero, epochZero.plusSeconds(1));
        AccessToken token = tokenCodec.encode(claims);

        TokenClaims decodedClaims = tokenCodec.decodeWithoutExpirationValidation(token);

        assertEquals(claims, decodedClaims);
    }

    @Test
    void decoding_an_malformed_accesstoken_throws_AccessTokenDecodingException() {
        AccessToken token = new AccessToken("abc");

        Exception exception = assertThrows(Exception.class,
            () -> tokenCodec.decodeWithoutExpirationValidation(token));

        assertEquals(AccessTokenDecodingException.class, exception.getClass());
        assertEquals("Cannot decode token 'abc'", exception.getMessage());
    }

    @Test
    void decoding_an_accesstoken_without_role_throws_AccessTokenDecodingException() {
        AccessToken token = getTokenWithoutRole();

        Exception exception = assertThrows(Exception.class,
            () -> tokenCodec.decodeWithoutExpirationValidation(token));

        assertEquals(AccessTokenDecodingException.class, exception.getClass());
        assertTrue(exception.getMessage().startsWith("Cannot decode token '" + token.value() + "'"));
    }

    @Test
    void decoding_an_accesstoken_with_invalid_role_array_claim_throws_AccessTokenDecodingException() {
        AccessToken token = getTokenWithInvalidRoleArrayClaim();

        Exception exception = assertThrows(Exception.class,
            () -> tokenCodec.decodeWithoutExpirationValidation(token));

        assertEquals(AccessTokenDecodingException.class, exception.getClass());
        assertTrue(exception.getMessage().startsWith("Cannot decode token '" + token.value() + "'"));
    }

    @Test
    void decoding_an_accesstoken_with_invalid_role_scalar_claim_throws_AccessTokenDecodingException() {
        AccessToken token = getTokenWithInvalidRoleScalarClaim();

        Exception exception = assertThrows(Exception.class,
            () -> tokenCodec.decodeWithoutExpirationValidation(token));

        assertEquals(AccessTokenDecodingException.class, exception.getClass());
        assertTrue(exception.getMessage().startsWith("Cannot decode token '" + token.value() + "'"));
    }

    @NotNull
    private TokenClaims getTokenClaims() {
        Instant now = Instant.now();
        return new TokenClaims(
            aUserPrincipal().withRole(Role.USER).build(),
            adjustPrecision(now),
            adjustPrecision(now.plusSeconds(1))
        );
    }


    // --------------------------------- Protected Methods ------------------------------- //
    protected abstract TokenClaimsCodecPort getInstance();
    protected Instant adjustPrecision(Instant instant) {
        return instant;
    }
    protected abstract AccessToken getTokenWithoutRole();
    protected abstract AccessToken getTokenWithInvalidRoleArrayClaim();
    protected abstract AccessToken getTokenWithInvalidRoleScalarClaim();
}
