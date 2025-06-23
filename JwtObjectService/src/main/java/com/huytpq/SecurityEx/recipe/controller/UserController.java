package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.base.security.JwtFilter;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeInput;
import com.huytpq.SecurityEx.recipe.dto.input.UserUpdateInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;
import com.huytpq.SecurityEx.recipe.dto.output.UserOutput;
import com.huytpq.SecurityEx.recipe.entity.Role;
import com.huytpq.SecurityEx.recipe.entity.User;
import com.huytpq.SecurityEx.recipe.service.impl.AccountService;
import com.huytpq.SecurityEx.recipe.service.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final AccountService accountService;

    @Autowired
    private final UserService userService;

    @GetMapping("/info")
    public String greet(HttpServletRequest request) {
        return "Welcome sir, this session is " + request.getSession().getId();
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> userInfo = accountService.getUserInfo(userDetails);
        return ResponseEntity.ok(userInfo);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/admin")
    public String adminAccess() {
        return "Admin Content";
    }

    @PostMapping("/details")
    public ResponseEntity<UserOutput> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);

            return ResponseEntity.ok(UserOutput.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody UserUpdateInput input) {
        userService.update(id, input);
        return ResponseEntity.ok("Successful");
    }

    @PostMapping("/get-list")
    public ResponseEntity<List<UserOutput>> getUserList(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String displayName) {
        try {
            String extractedToken = token.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            List<User> users = userService.getUserListFromToken(extractedToken, displayName);

            List<UserOutput> userOutputs = users.stream()
                    .map(UserOutput::fromUser)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userOutputs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}
