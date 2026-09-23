package com.voicehub.service;

import com.voicehub.model.User;
import com.voicehub.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(String email, String username, String password) {
        if (userRepository.existsByEmail(email)) {
            return false;
        }
        userRepository.save(new User(email, username, password));
        return true;
    }

    public User login(String email, String password) {
        return userRepository.findByEmail(email)
            .filter(user -> user.getPassword().equals(password))
            .orElse(null);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
