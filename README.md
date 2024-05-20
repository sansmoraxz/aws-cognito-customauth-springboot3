# Cognito Authorization with Spring Boot 3

This is a native implementation of cognito authorization with spring boot 3. It uses the jwk url to validate the token. A custom [`JwtDecoder`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/jwt/JwtDecoder.html) is implemented to validate the token. It is highly customizable and can be used with any spring boot 3 application. Refer to the [`SecurityConfig`](src/main/java/com/example/cogauthpoc/configurations/SecurityConfig.java) file to see how it's done.

Config logic in above link. Configuration used are in `application.yml` file.

Sample config:

```yaml
aws:
  cognito:
    user-pool-id: us-east-1_4ABCdeFgh
    audiences: XXXXXXXXXXX,YYYYYYYYYYY
    region: us-east-1
```

## How to run

1. Clone the repository
2. Start the application using `./gradlew bootRun`
3. Get a token from cognito and use it to access the endpoints. You may refer to this [link](https://docs.aws.amazon.com/cognito/latest/developerguide/token-endpoint.html) to get the token. Alternatively you may run the shell script at this [link](https://gist.github.com/Can-Sahin/377fee56a53a0a31bf9e5ce7f9847ac2).
4. Test endpoints using postman or curl viz. `curl -X GET http://localhost:8866/ --header 'Authorization: Bearer <token>'`
