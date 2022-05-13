package com.ajmanre.payload.response;

import com.ajmanre.models.Role;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class User {

    private String id;

    private String username;

    private String email;

    private List<Role> roles;
}
