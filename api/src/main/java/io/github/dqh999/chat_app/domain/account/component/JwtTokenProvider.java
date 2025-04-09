package io.github.dqh999.chat_app.domain.account.component;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.github.dqh999.chat_app.domain.account.data.dto.TokenDTO;
import io.github.dqh999.chat_app.domain.account.data.dto.TokenMetadataDTO;
import io.github.dqh999.chat_app.domain.account.exception.AccountException;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.utils.ResourceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private long expiration;

    private static final String CLAIM_ROLE = "role";

    public TokenDTO generateTokens(TokenMetadataDTO tokenMetadata) {
        Date issuedAt = tokenMetadata.issuedAt();
        Date accessValidity = new Date(issuedAt.getTime() + expiration * 1000);
        Date refreshValidity = new Date(issuedAt.getTime() + expiration * 1500);

        String accessToken = generateToken(tokenMetadata.userId(), tokenMetadata.roles(), issuedAt, accessValidity);
        String refreshToken = generateToken(tokenMetadata.userId(), null, issuedAt, refreshValidity);

        return new TokenDTO(tokenMetadata.userId(), accessToken, accessValidity, refreshToken, refreshValidity);
    }

    private String generateToken(String userId, List<String> roles, Date issuedAt, Date expiration) {
        try {
            var claimsSetBuilder = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issuer("auth-service")
                    .issueTime(issuedAt)
                    .expirationTime(expiration)
                    .jwtID(UUID.randomUUID().toString());
            if (roles != null) {
                claimsSetBuilder.claim(CLAIM_ROLE, roles);
            }

            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS256),
                    new Payload(claimsSetBuilder.build().toJSONObject())
            );

            jwsObject.sign(new MACSigner(secretKey));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ResourceException.UNEXPECTED_ERROR);
        }
    }

    public Optional<JWTClaimsSet> verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSObject jwsObject = JWSObject.parse(token);

            if (!jwsObject.verify(new MACVerifier(secretKey))) {
                return Optional.empty();
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();

            if (expirationTime != null && expirationTime.before(new Date())) {
                return Optional.empty();
            }

            return Optional.of(claims);
        } catch (ParseException | JOSEException e) {
            throw new AppException(ResourceException.UNEXPECTED_ERROR);
        }
    }

    public boolean validateToken(String token) {
        return verifyToken(token).isPresent();
    }

    public Date getExpirationFromToken(String token) {
        return verifyToken(token)
                .map(JWTClaimsSet::getExpirationTime)
                .orElseThrow(() -> new AppException(AccountException.INVALID_TOKEN));
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromClaims(JWTClaimsSet claims) {
        return (List<String>) claims.getClaim(CLAIM_ROLE);
    }
}
