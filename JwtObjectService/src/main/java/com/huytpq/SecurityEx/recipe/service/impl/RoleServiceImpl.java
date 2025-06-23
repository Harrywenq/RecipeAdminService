package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.recipe.entity.Role;
import com.huytpq.SecurityEx.recipe.repo.RoleRepo;
import com.huytpq.SecurityEx.recipe.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;

    @Override
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }
}

