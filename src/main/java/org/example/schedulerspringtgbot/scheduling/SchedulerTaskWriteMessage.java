package org.example.schedulerspringtgbot.scheduling;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schedulerspringtgbot.bot.Bot;
import org.example.schedulerspringtgbot.model.entity.User;
import org.example.schedulerspringtgbot.model.enums.Companion;
import org.example.schedulerspringtgbot.service.AiService;
import org.example.schedulerspringtgbot.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerTaskWriteMessage {

    private final Bot bot;
    private final UserService userService;
    private final AiService aiService;

//    @Scheduled(fixedRate = 20000)
    public void sendScheduledMessage() {
        List<User> users = userService.findAll();
        log.info("Все пользователи которые есть {}", users.toString());

        users.forEach(user -> {
            Companion companion = user.getCompanion();

            String textRequest = " представь что ты мой " + companion.name() + " начни диалог с любой темы";
//            String textAi = aiService.responseAi(textRequest);

//            bot.sendMessage(user.getChatId(), textAi);
        });
    }

}
