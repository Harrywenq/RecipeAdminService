package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeInput;
import com.huytpq.SecurityEx.recipe.dto.input.RegisterInput;
import com.huytpq.SecurityEx.recipe.dto.input.UserUpdateInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RegisterOutput;
import com.huytpq.SecurityEx.recipe.dto.output.UserOutput;
import com.huytpq.SecurityEx.recipe.entity.*;
import com.huytpq.SecurityEx.recipe.mapper.ModelMapper;
import com.huytpq.SecurityEx.recipe.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RecipeRepo recipeRepo;
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private FavoriteRecipeRepo favoriteRecipeRepo;
    @Autowired
    private PostTagRepo postTagRepo;
    @Autowired
    private TagRepo tagRepo;
    @Autowired
    private RecipeImageRepo recipeImageRepo;
    @Autowired
    private RecipeTagRepo recipeTagRepo;

    public RegisterOutput register(RegisterInput input) {
        if (input.getUsername() == null || input.getPassword() == null || input.getRole() == null) {
            throw new IllegalArgumentException("Username, password, and role are required.");
        }

        if (userRepo.findByUsername(input.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }

        String encodedPassword = encoder.encode(input.getPassword());

        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(encodedPassword);
        user.setRetypePassword(encodedPassword);
        user.setDateOfBirth(input.getDateOfBirth());
        user.setDisplayName(input.getDisplayName());
        user = userRepo.save(user);

        Role role = roleRepo.findByName(input.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found: " + input.getRole()));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepo.save(userRole);

        RegisterOutput output = new RegisterOutput();
        output.setId(user.getId());
        output.setUsername(user.getUsername());
        output.setPassword(user.getPassword());
        output.setRetypePassword(user.getRetypePassword());
        output.setDateOfBirth(user.getDateOfBirth());
        output.setDisplayName(user.getDisplayName());
        output.setRole(role.getName());

        return output;
    }

    public String verify(String username, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        User existingUser = optionalUser.get();

        if (!encoder.matches(password, existingUser.getPassword())) {
            throw new RuntimeException("Wrong password.");
        }

        List<UserRole> userRoles = userRoleRepo.findByUserId(existingUser.getId());

        boolean hasRole = userRoles.stream()
                .anyMatch(userRole -> userRole.getRole() != null && userRole.getRole().getId().equals(roleId));

        if (!hasRole) {
            throw new RuntimeException("User does not have the required role.");
        }

        // Xác thực người dùng bằng UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return JWTService.generateToken(username, authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
    }

    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtService.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }

        String userName = jwtService.extractUsername(token);

        User user = userRepo.findByUsername(userName)
                .orElseThrow(() -> new Exception("User not found with username: " + userName));

        List<UserRole> userRoles = userRoleRepo.findByUser(user);
        if (userRoles.isEmpty()) {
            throw new Exception("No role found for user: " + userName);
        }

        user.setRole(userRoles.get(0).getRole());

        return user;
    }

    public void update(Long id, UserUpdateInput input) {
        var record =
                userRepo
                        .findById(id)
                        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        var updateUser = modelMapper.updateUser(record, input);
        userRepo.save(updateUser);
    }

    public List<User> getUserListFromToken(String token, String displayName) throws Exception {
        if (jwtService.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }

        String userName = jwtService.extractUsername(token);

        User authenticatedUser = userRepo.findByUsername(userName)
                .orElseThrow(() -> new Exception("User not found with username: " + userName));

        List<User> users = userRepo.findByCondition(displayName);

        for (User user : users) {
            List<UserRole> userRoles = userRoleRepo.findByUser(user);
            if (!userRoles.isEmpty()) {
                user.setRole(userRoles.get(0).getRole());
            } else {
                user.setRole(null);
            }
        }

        return users;
    }

//    @Transactional
//    public void delete(Long id) {
//        User user = userRepo.findById(id)
//                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
//        userRoleRepo.deleteByUserId(id);
//        recipeRepo.deleteByUserId(id);
//        postRepo.deleteByUserId(id);
//        favoriteRecipeRepo.deleteByUserId(id);
//        postTagRepo.deleteByPostId(id);
//
//        userRepo.delete(user);
//    }

    @Transactional
    public void delete(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        userRoleRepo.deleteByUserId(userId);

        favoriteRecipeRepo.deleteByUserId(userId);

        List<Post> posts = postRepo.findByUserId(userId);
        for (Post post : posts) {
            postTagRepo.deleteByPostId(post.getId());
        }
        postRepo.deleteAll(posts);

        List<Recipe> recipes = recipeRepo.findByUserId(userId);
        for (Recipe recipe : recipes) {
            recipeTagRepo.deleteByRecipeId(recipe.getId());
            recipeImageRepo.deleteByRecipeId(recipe.getId());
        }
        recipeRepo.deleteAll(recipes);

        userRepo.delete(user);
    }

}