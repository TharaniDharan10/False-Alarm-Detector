package com.example.False.Alarm.controller;

import com.example.False.Alarm.dto.AddUserRequest;
import com.example.False.Alarm.dto.UserSearchDTO;
import com.example.False.Alarm.model.User;
import com.example.False.Alarm.service.ChatMonitorService;
import com.example.False.Alarm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    private ChatMonitorService chatMonitorService;

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
        ResponseEntity<?> response = userService.addAdmin(path,addUserRequest);
        return response;

    }

    @CrossOrigin(origins = "*")
    @PostMapping("/chat/{userId}")
    public ResponseEntity<List<String>> checkChatMessage(@PathVariable String userId, @RequestBody String message) {
        if (chatMonitorService.isBlocked(userId)) {
            return ResponseEntity.ok(List.of("🚫 You are blocked. Type '/reset' to clear warnings."));
        }
        return ResponseEntity.ok(chatMonitorService.checkMessage(userId, message));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/chat/reset/{userId}")
    public ResponseEntity<String> resetChatStatus(@PathVariable String userId) {
        return ResponseEntity.ok(chatMonitorService.resetCounts(userId));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        List<User> usersByName = userService.searchByUsername(query);
        List<User> usersById = userService.searchByUserId(query);

        Set<User> combined = new LinkedHashSet<>();
        combined.addAll(usersByName);
        combined.addAll(usersById);

        return ResponseEntity.ok(new ArrayList<>(combined));
    }

}
