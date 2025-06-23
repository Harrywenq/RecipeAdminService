package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOutput {
    private Long id;
    private String username;
    private String password;
    private String retypePassword;
    private LocalDate dateOfBirth;
    private String displayName;
    private String role;
}
