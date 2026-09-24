package com.studyroom.controller;

import com.studyroom.model.AuthRequest;
import com.studyroom.model.AuthResponse;
import com.studyroom.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return new AuthResponse(false, "Email không được để trống");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return new AuthResponse(false, "Tên hiển thị không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return new AuthResponse(false, "Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (userService.emailExists(request.getEmail())) {
            return new AuthResponse(false, "Email đã được sử dụng");
        }

        boolean registered = userService.register(
            request.getEmail(), request.getUsername(), request.getPassword()
        );

        if (registered) {
            return new AuthResponse(true, "Đăng ký thành công!");
        }
        return new AuthResponse(false, "Đăng ký thất bại");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return new AuthResponse(false, "Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return new AuthResponse(false, "Mật khẩu không được để trống");
        }

        var user = userService.login(request.getEmail(), request.getPassword());
        if (user != null) {
            return new AuthResponse(true, "Đăng nhập thành công!", user.getUsername(), user.getEmail());
        }
        return new AuthResponse(false, "Email hoặc mật khẩu không đúng");
    }
}

