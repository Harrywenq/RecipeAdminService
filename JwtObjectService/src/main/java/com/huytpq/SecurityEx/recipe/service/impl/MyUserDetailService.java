package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.recipe.entity.Role;
import com.huytpq.SecurityEx.recipe.entity.User;
import com.huytpq.SecurityEx.recipe.entity.UserPrinciple;
import com.huytpq.SecurityEx.recipe.repo.RoleRepo;
import com.huytpq.SecurityEx.recipe.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private RoleRepo roleRepo;

    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<Role> roles = roleRepo.findRolesByUserId(user.getId());
        return new UserPrinciple(user,roles);
    }
}
