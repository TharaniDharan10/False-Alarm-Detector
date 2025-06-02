package com.example.False.Alarm.controller;

import com.example.False.Alarm.dto.AddUserRequest;
import com.example.False.Alarm.dto.OAuthUserInfo;
import com.example.False.Alarm.dto.UserSearchDTO;
import com.example.False.Alarm.model.User;
import com.example.False.Alarm.service.ChatMonitorService;
import com.example.False.Alarm.service.OAuthService;
import com.example.False.Alarm.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    OAuthService oAuthService;

    @Value("${project.image}")
    private String path;

    @CrossOrigin(origins = "*")
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@ModelAttribute @Valid AddUserRequest addUserRequest) throws IOException {
        ResponseEntity<?> response = userService.addUser(path,addUserRequest);
        return response;

    }

    @CrossOrigin(origins = "*")
    @PostMapping("/admin")
    public ResponseEntity<?> addAdmin(@ModelAttribute @Valid AddUserRequest addUserRequest) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuthUserInfo userInfo = oAuthService.getUserInfo(authentication);
        log.info("Hi again, "+ userInfo.getLogin() + " ! Your token is " + userInfo.getAccessToken());
        ResponseEntity<?> response = userService.addAdmin(path,addUserRequest);
        return response;

    }


    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuthUserInfo userInfo = oAuthService.getUserInfo(authentication);
        log.info("Hi again, "+ userInfo.getLogin() + " ! Your token is " + userInfo.getAccessToken());
        List<User> usersByName = userService.searchByUsername(query);
        List<User> usersById = userService.searchByUserId(query);

        Set<User> combined = new LinkedHashSet<>();
        combined.addAll(usersByName);
        combined.addAll(usersById);

        return ResponseEntity.ok(new ArrayList<>(combined));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/demo")
    public String demoForOAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            return "You are not logged in via OAuth2.";
        }
        OAuthUserInfo userInfo = oAuthService.getUserInfo(authentication);

        log.info("user:"+ authentication.getPrincipal());

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String login = user.getAttribute("login") != null ? user.getAttribute("login") : "Guest";

        return "Hello, ".concat(login).concat("You are logged in via OAuth2.");
    }


}
