package com.ajmanre.controllers;

import com.ajmanre.models.Requirement;
import com.ajmanre.models.RoleEnum;
import com.ajmanre.payload.response.Page;
import com.ajmanre.repository.UserRepository;
import com.ajmanre.security.jwt.JwtUtils;
import com.ajmanre.services.UserService;
import org.openapitools.api.UserApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController implements UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/justLegacyAPI")
    public ResponseEntity<Page<com.ajmanre.payload.response.User>> get(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int size) {
        Page<com.ajmanre.payload.response.User> page = userService.getPage(pageNo, size);
        return ResponseEntity.ok(page);
    }

    @Override
    public ResponseEntity<List<org.openapitools.model.User>> userAll() {
        List<User> users  = userRepository.findAll().stream().map(this::toOut).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserPage> userPageSize(Integer page, Integer size) {

        org.springframework.data.domain.Page<com.ajmanre.models.User> pg = userService.getPageNew(page, size);
        if(null != pg.getContent() && !pg.getContent().isEmpty()) {
            List<User> users = pg.getContent().stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(new UserPage().page(page).size(pg.getSize()).data(users).total(pg.getTotalElements()));
        }
        return ResponseEntity.ok(new UserPage().page(page).size(0).data(null).total(0l));
    }

    private com.ajmanre.models.User toModel(User user) {
        com.ajmanre.models.User usr = new com.ajmanre.models.User();
        return usr;
    }

    private User toOut(com.ajmanre.models.User user) {

        OffsetDateTime updatedAt = null;
        if(null != user.getUpdatedAt()) {
            updatedAt = user.getUpdatedAt().atOffset(ZoneOffset.UTC);
        }

        List<String> roles = null;
        if(null != user.getRoles()) {
            roles = user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList());
        }

        return new User().id(user.getId()).updatedAt(updatedAt).name(user.getName()).username(user.getUsername())
                .email(user.getEmail()).roles(roles);
    }

    @Override
    public ResponseEntity<MessageResponse> changePassword(ChangePassword changePassword, String authorization) {

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            final String username = jwtUtils.getUserNameFromJwtToken(jwt);

            boolean isAdmin = userRepository.findByUsername(username).get()
                    .getRoles().stream().anyMatch(r -> r.getName().equals(RoleEnum.ROLE_ADMIN));


            if(username.equals(changePassword.getUsername()) || isAdmin) {
                com.ajmanre.models.User user = userRepository.findByUsername(changePassword.getUsername()).get();
                user.setPassword(encoder.encode(changePassword.getPassword()));
                userRepository.save(user);
                return ResponseEntity.ok(new MessageResponse().message("Password changed successfully").identifier(user.getId()));
            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse().message("Change password can only be performed by admin or user itself."));
    }
}
