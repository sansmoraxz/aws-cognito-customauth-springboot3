package com.example.cogauthpoc.configurations;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class CognitoConfig {
    @Value("${aws.cognito.user-pool-id}")
    String userPoolId;

    @Value("${aws.cognito.audiences}")
    List<String> audiences;

    @Value("${aws.cognito.region}")
    String region;

    public String getIss() {
        return "https://cognito-idp." + region + ".amazonaws.com/" + userPoolId;
    }

    public String getJwkSetUri() {
        return getIss() + "/.well-known/jwks.json";
    }
}
