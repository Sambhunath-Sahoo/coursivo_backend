package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.dto.auth.AuthResponse;
import com.coursivo.coursivo_backend.dto.auth.LoginRequest;
import com.coursivo.coursivo_backend.dto.auth.RegisterRequest;
import com.coursivo.coursivo_backend.dto.common.ApiResponse;
import com.coursivo.coursivo_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse auth = authService.register(request);
		return ResponseEntity.ok(ApiResponse.ok(auth, "Registration successful"));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse auth = authService.login(request);
		return ResponseEntity.ok(ApiResponse.ok(auth, "Login successful"));
	}

}
