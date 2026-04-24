package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.auth.AuthResponse;
import com.coursivo.coursivo_backend.dto.auth.LoginRequest;
import com.coursivo.coursivo_backend.dto.auth.RegisterRequest;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.UserRole;
import com.coursivo.coursivo_backend.repository.UserRepository;
import com.coursivo.coursivo_backend.security.CustomUserDetails;
import com.coursivo.coursivo_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	private final JwtUtil jwtUtil;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new IllegalArgumentException("Email is already registered.");
		}

		if (request.role() == UserRole.ADMIN) {
			throw new IllegalArgumentException("Cannot self-register as ADMIN.");
		}

		String hashedPassword = passwordEncoder.encode(request.password());

		User user = User.builder()
			.email(request.email())
			.password(hashedPassword)
			.fullName(request.fullName())
			.role(request.role())
			.isActive(true)
			.build();

		userRepository.save(user);

		String token = jwtUtil.generateToken(user);
		return AuthResponse.bearer(token);
	}

	public AuthResponse login(LoginRequest request) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.email(),
				request.password());

		Authentication authentication = authenticationManager.authenticate(authToken);

		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		String token = jwtUtil.generateToken(principal.getUser());
		return AuthResponse.bearer(token);
	}

}
