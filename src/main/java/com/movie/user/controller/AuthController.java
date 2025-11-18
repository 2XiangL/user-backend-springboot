package com.movie.user.controller;

import com.movie.user.dto.AuthRequest;
import com.movie.user.dto.LoginRequest;
import com.movie.user.dto.ApiResponse;
import com.movie.user.entity.User;
import com.movie.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody AuthRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/user/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/user/username/{username}")
    public ApiResponse<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/user/{id}/password")
    public ApiResponse<String> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        return userService.changePassword(id, oldPassword, newPassword);
    }

    @PutMapping("/user/{id}/profile")
    public ApiResponse<User> updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email) {
        return userService.updateProfile(id, fullName, email);
    }
}

