package com.example.cogauthpoc.configurations;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.config.Customizer;

import org.springframework.security.oauth2.core.OAuth2Error;

@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CognitoConfig cognitoConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        var decoder =  NimbusJwtDecoder
            .withJwkSetUri(cognitoConfig.getJwkSetUri())
            .build();
        decoder.setJwtValidator(
            // Ref: https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-the-id-token.html
            new OAuth2TokenValidator<Jwt>() {
                List<String> tokenUses = List.of("id", "access");

                @Override
                public OAuth2TokenValidatorResult validate(Jwt token) {
                    var claims = token.getClaims();
                    log.debug("Claims: {}", claims);
                    if (!claims.get("iss").equals(cognitoConfig.getIss())) {
                        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_iss_claim", "Invalid issuer", null));
                    }
                    
                    if (!tokenUses.contains(claims.get("token_use"))) {
                        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token_use", "Invalid token use", null));
                    }

                    if (((Instant) claims.get("exp")).isBefore(Instant.now())) {
                        return OAuth2TokenValidatorResult.failure(new OAuth2Error("token_expired", "Token expired at " + claims.get("exp"), null));
                    }


                    // audience validation
                    if(cognitoConfig.getAudiences().size() == 0) {
                        return OAuth2TokenValidatorResult.success();
                    }
                    @SuppressWarnings("unchecked")
                    List<String> claimedAudience = (List<String>) claims.get("aud");
                    for(String registeredAudience: cognitoConfig.getAudiences()) {

                        if (claimedAudience.contains(registeredAudience)) {
                            return OAuth2TokenValidatorResult.success();
                        }
                    }
                    return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_audience", "No matching audience found", null));
                }
            }
        );
        return decoder;
    }

}
