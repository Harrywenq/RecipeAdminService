package com.huytpq.SecurityEx.recipe.dto.output;

import com.huytpq.SecurityEx.recipe.entity.Role;
import com.huytpq.SecurityEx.recipe.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOutput {

    private Long id;
    private String username;
    private String displayName;
    private LocalDate dateOfBirth;
    private Long roleId;
    private String roleName;

    public static UserOutput fromUser(User user) {
        return UserOutput.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .dateOfBirth(user.getDateOfBirth())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }

}
