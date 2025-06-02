package com.example.False.Alarm.controller;

import com.example.False.Alarm.dto.OAuthUserInfo;
import com.example.False.Alarm.service.ChatMonitorService;
import com.example.False.Alarm.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/users")
public class ConversationController {

    @Autowired
    private ChatMonitorService chatMonitorService;

    @Autowired
    OAuthService oAuthService;

    @CrossOrigin(origins = "*")
    @PostMapping("/chat/{userId}")
    public ResponseEntity<List<String>> checkChatMessage(@PathVariable String userId, @RequestBody String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuthUserInfo userInfo = oAuthService.getUserInfo(authentication);
        log.info("Hi again, "+ userInfo.getLogin() + " ! Your token is " + userInfo.getAccessToken());
        if (chatMonitorService.isBlocked(userId)) {
            return ResponseEntity.ok(List.of("🚫 You are blocked. Type '/reset' to clear warnings."));
        }
        return ResponseEntity.ok(chatMonitorService.checkMessage(userId, message));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/chat/reset/{userId}")
    public ResponseEntity<String> resetChatStatus(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuthUserInfo userInfo = oAuthService.getUserInfo(authentication);
        log.info("Hi again, "+ userInfo.getLogin() + " ! Your token is " + userInfo.getAccessToken());
        return ResponseEntity.ok(chatMonitorService.resetCounts(userId));
    }

}
