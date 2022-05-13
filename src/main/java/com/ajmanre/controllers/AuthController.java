package com.ajmanre.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.openapitools.api.AuthApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajmanre.models.RoleEnum;
import com.ajmanre.models.Role;
import com.ajmanre.models.User;
import com.ajmanre.payload.request.LoginRequest;
import com.ajmanre.payload.request.SignupRequest;
import com.ajmanre.payload.response.JwtResponse;
import com.ajmanre.payload.response.MessageResponse;
import com.ajmanre.repository.RoleRepository;
import com.ajmanre.repository.UserRepository;
import com.ajmanre.security.jwt.JwtUtils;
import com.ajmanre.security.services.UserDetailsImpl;
import org.springframework.web.context.request.NativeWebRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthController implements AuthApi {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return AuthApi.super.getRequest();
	}

	@Override
	public ResponseEntity<org.openapitools.model.JwtResponse> signin(org.openapitools.model.LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		org.openapitools.model.JwtResponse jwtResponse = new org.openapitools.model.JwtResponse();
		jwtResponse.setAccessToken(jwt);
		jwtResponse.setType("Bearer");
		jwtResponse.setId(userDetails.getId());
		jwtResponse.setUsername(userDetails.getUsername());
		jwtResponse.setEmail(userDetails.getEmail());
		jwtResponse.setRoles(roles);
		return ResponseEntity.ok(jwtResponse);
	}

	@Override
	public ResponseEntity<org.openapitools.model.MessageResponse> signup(org.openapitools.model.SignupRequest signupRequest) {

		org.openapitools.model.MessageResponse response = new org.openapitools.model.MessageResponse();
		if (userRepository.existsByUsername(signupRequest.getUsername())) {
			response.setMessage("Error: Username is already taken!");
			return ResponseEntity
					.badRequest()
					.body(response);
		}

		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			response.setMessage("Error: Email is already in use!");
			return ResponseEntity
					.badRequest()
					.body(response);
		}

		// Create new user's account
		User user = new User(signupRequest.getUsername(),
				signupRequest.getEmail(),
				encoder.encode(signupRequest.getPassword()));

		List<String> strRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;
					case "agent":
						Role modRole = roleRepository.findByName(RoleEnum.ROLE_AGENT)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(modRole);

						break;
					default:
						Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);
		response.setMessage("User registered successfully!");
		return ResponseEntity.ok(response);
	}
}
