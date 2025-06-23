package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.recipe.entity.Role;
import com.huytpq.SecurityEx.recipe.entity.User;
import com.huytpq.SecurityEx.recipe.repo.UserRepo;
import com.huytpq.SecurityEx.recipe.repo.UserRoleRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    public AccountService(UserRepo userRepo, UserRoleRepo userRoleRepo) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
    }
    public Map<String, Object> getUserInfo(UserDetails userDetails) {
        if (userDetails == null) {
            return Map.of("error", "No user is currently logged in.");
        }
        // Tìm user theo usernames
        Optional<User> user = userRepo.findByUsername(userDetails.getUsername());
        // Lấy danh sách roles từ bảng UserRole
        List<Role> roles = userRoleRepo.findRolesByUserId(user.stream().findFirst().get().getId());
        // Nếu không có roles, trả về thông tin mặc định
        if (roles == null || roles.isEmpty()) {
            return Map.of(
                    "username", user.stream().findFirst().get().getUsername(),
                    "roles", "No roles found"
            );
        }
        // Lấy danh sách tên roles
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        // Trả về thông tin user
        return Map.of(
                "username", user.stream().findFirst().get().getUsername(),
                "roles", roleNames
        );
    }
}

