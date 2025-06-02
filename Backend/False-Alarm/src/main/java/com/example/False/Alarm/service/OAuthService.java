package com.example.False.Alarm.service;


import com.example.False.Alarm.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final OAuth2AuthorizedClientService clientService;

    public OAuthUserInfo getUserInfo(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            throw new IllegalStateException("User is not authenticated with OAuth2");
        }
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        String login = user.getAttribute("login") != null ? user.getAttribute("login") : "Guest";
        String company = user.getAttribute("company") != null ? user.getAttribute("company") : "Unknown Company";

        log.info("user: {}", login);
        log.info("access token: {}", accessToken);

        return new OAuthUserInfo(login, company, accessToken);
    }
}
