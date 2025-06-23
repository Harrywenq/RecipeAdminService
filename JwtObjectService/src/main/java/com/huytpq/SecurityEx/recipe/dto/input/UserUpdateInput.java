package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateInput {
    private String password;

    private String retypePassword;

    private String displayName;

    private LocalDate dateOfBirth;

}
