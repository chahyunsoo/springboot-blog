package org.blog.springbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddUserRequest {
    private String email;
    private String password;
    private String auth;
}
