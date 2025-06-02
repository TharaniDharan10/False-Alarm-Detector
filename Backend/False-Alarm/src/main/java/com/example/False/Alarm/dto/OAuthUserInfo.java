package com.example.False.Alarm.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuthUserInfo {
    private String login;
    private String company;
    private String accessToken;
}