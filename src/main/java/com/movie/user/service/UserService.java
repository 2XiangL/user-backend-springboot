package com.movie.user.service;

import com.movie.user.dto.AuthRequest;
import com.movie.user.dto.LoginRequest;
import com.movie.user.dto.ApiResponse;
import com.movie.user.entity.User;

public interface UserService {
    ApiResponse<User> register(AuthRequest request);

    ApiResponse<String> login(LoginRequest request);

    ApiResponse<User> getUserById(Long id);

    ApiResponse<User> getUserByUsername(String username);

    ApiResponse<String> changePassword(Long userId, String oldPassword, String newPassword);

    ApiResponse<User> updateProfile(Long userId, String fullName, String email);
}

