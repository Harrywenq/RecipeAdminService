package com.huytpq.SecurityEx.base.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LoginInput {
    private String username;
    private String password;
    private Long roleId;
}
