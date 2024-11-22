package org.example.schedulerspringtgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.schedulerspringtgbot.model.entity.User;
import org.example.schedulerspringtgbot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean save(User user) {
        if(userRepository.findByUsername(user.getUsername()) == null) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Long findChatIdByUsername(String username) {
        return userRepository.findByUsername(username).getChatId();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
