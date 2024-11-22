package org.example.schedulerspringtgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.schedulerspringtgbot.model.entity.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;

    public boolean telegramChatRegistration(Update update) {
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();

        User user = new User();
        user.setUsername(username);
        user.setChatId(chatId);
        return userService.save(user);
    }

    public boolean gptChatRegistration(String chatId){
        User user = new User();
//        user.setGptChatId(chatId);

        return userService.save(user);
    }
}
