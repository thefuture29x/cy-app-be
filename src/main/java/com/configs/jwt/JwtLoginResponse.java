package com.config.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtLoginResponse {
    private String token;
    private String type;
    private Long timeValid;
    private List<String> authorities;
}
