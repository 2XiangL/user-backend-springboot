package com.movie.user.service.impl;

import com.movie.user.dto.AuthRequest;
import com.movie.user.dto.LoginRequest;
import com.movie.user.dto.ApiResponse;
import com.movie.user.entity.User;
import com.movie.user.repository.UserRepository;
import com.movie.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<User> register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("用户名已存在");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return ApiResponse.success("注册成功", savedUser);
    }

    @Override
    public ApiResponse<String> login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户名或密码错误");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error("用户名或密码错误");
        }

        return ApiResponse.success("登录成功");
    }

    @Override
    public ApiResponse<User> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }
        return ApiResponse.success(userOpt.get());
    }

    @Override
    public ApiResponse<User> getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }
        return ApiResponse.success(userOpt.get());
    }

    @Override
    public ApiResponse<String> changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ApiResponse.error("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ApiResponse.success("密码修改成功");
    }

    @Override
    public ApiResponse<User> updateProfile(Long userId, String fullName, String email) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        User user = userOpt.get();

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                return ApiResponse.error("邮箱已被使用");
            }
            user.setEmail(email);
        }

        if (fullName != null) {
            user.setFullName(fullName);
        }

        User savedUser = userRepository.save(user);
        return ApiResponse.success("个人信息更新成功", savedUser);
    }
}

