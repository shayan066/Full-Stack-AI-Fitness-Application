package com.fitness.gateway.config;

import com.fitness.gateway.model.request.RegisterRequest;
import com.fitness.gateway.service.UserServiceImpl;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KeycloakUserSyncFilter implements WebFilter {
    private final UserServiceImpl userService;


    public KeycloakUserSyncFilter(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Void>filter(ServerWebExchange exchange, WebFilterChain chain){
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null){
            userId = registerRequest.getKeyCloakId();
        }

        if(userId != null && token != null){
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if(!exist){
                            if(registerRequest != null){
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }else{
                                return Mono.empty();
                            }
                        }else{
                            log.info("User already exist, Skipping sync.");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest()
                                .mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));

        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            return RegisterRequest.builder()
                    .keyCloakId(claims.getStringClaim("sub"))
                    .firstName(claims.getStringClaim("given_name"))
                    .lastName(claims.getStringClaim("family_name"))
                    .email(claims.getStringClaim("email"))
                    .password("dummy@123")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
