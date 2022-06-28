package com.ajmanre.controllers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.ajmanre.repository.AgencyRepository;
import com.ajmanre.repository.AgentRepository;
import com.ajmanre.services.UserService;
import org.openapitools.api.AuthApi;
import org.openapitools.model.Source;
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

	@Autowired
	UserService userService;

	@Autowired
	AgentRepository agentRepository;

	@Autowired
	AgencyRepository agencyRepository;

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

		com.ajmanre.models.User user = userRepository.findByUsername(userDetails.getUsername()).get();
		org.openapitools.model.JwtResponse jwtResponse = new org.openapitools.model.JwtResponse();
		jwtResponse.setAccessToken(jwt);
		jwtResponse.setType("Bearer");
		jwtResponse.setId(userDetails.getId());
		jwtResponse.setUsername(userDetails.getUsername());
		jwtResponse.setName(user.getName());
		jwtResponse.setEmail(userDetails.getEmail());
		jwtResponse.setRoles(roles);

		agentRepository.findByUserId(user.getId()).ifPresent(agent -> {
			jwtResponse.setAgent(new Source().id(agent.getId()).name(agent.getName()));
			if(null != agent.getAgency()) {
				jwtResponse.setAgency(new Source().id(agent.getAgency().getId()).name(agent.getAgency().getName()));
			}
		});

		agencyRepository.findByUserId(user.getId()).ifPresent(agency -> {
			jwtResponse.setAgency(new Source().id(agency.getId()).name(agency.getName()));
		});

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

		user.setName(signupRequest.getName());
		user.setEmail(signupRequest.getEmail());
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
					case "agency":
						Role agencyRole = roleRepository.findByName(RoleEnum.ROLE_AGENCY)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(agencyRole);

						break;
					case "owner":
						Role ownRole = roleRepository.findByName(RoleEnum.ROLE_OWNER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(ownRole);
						break;
					default:
						Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		user.setUpdatedAt(LocalDateTime.now());
		user = userRepository.save(user);
		if(null != strRoles && strRoles.contains("agent")) {
			userService.agentCreated(user);
		} else if(null != strRoles && strRoles.contains("agency")) {
			userService.agencyCreated(user, signupRequest.getAgencyName());
		}

		response.message("User registered successfully!").identifier(user.getId());
		return ResponseEntity.ok(response);
	}
}
