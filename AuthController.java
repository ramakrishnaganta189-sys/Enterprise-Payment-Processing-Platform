package com.bankcard.paymentservice.controller;

import com.bankcard.paymentservice.dto.AuthRequest;
import com.bankcard.paymentservice.dto.AuthResponse;
import com.bankcard.paymentservice.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Issues bearer tokens for the demo. In production this endpoint would
 * delegate to an OAuth2 Authorization Server rather than checking
 * credentials in-process.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // Demo-only credential check. Replace with a real UserDetailsService
        // + PasswordEncoder comparison, or delegate to an OAuth2 provider.
        if (!"demo-user".equals(request.username()) || !"demo-pass".equals(request.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(request.username(), List.of("USER"));
        return ResponseEntity.ok(AuthResponse.bearer(token, jwtUtil.getExpirationSeconds()));
    }
}
