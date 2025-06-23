package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class RegisterInput {
    private String username;
    private String password;
    private String retypePassword;
    private LocalDate dateOfBirth;
    private String displayName;
    private String role;
}
