package org.example.schedulerspringtgbot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schedulerspringtgbot.service.AiService;
import org.example.schedulerspringtgbot.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private final UserRegistrationService registrationService;
    private final AiService aiService;

    @Value(value = "${telegrambots.bot.username}")
    private String username;

    @Value(value = "${telegrambots.bot.token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        if (isMessageAndText(update)) {
            processUserMessage(update);
        } else {
            log.info("Получено сообщение без текста или некорректное обновление.");
        }
    }

    private void processUserMessage(Update update) {
        try {
            log.info("Обработка сообщения от пользователя: {}", update.getMessage().getFrom().getId());

            boolean isRegistered = registrationService.telegramChatRegistration(update);
            if (isRegistered) {
                log.info("Пользователь {} успешно зарегистрирован.", update.getMessage().getFrom().getId());
            } else {
                log.warn("Пользователь {} уже зарегистрирован или не удалось зарегистрировать.", update.getMessage().getFrom().getId());
            }

            String textRequest = getUserMessage(update);
            log.info("Получено сообщение от пользователя: {}", textRequest);

            String response = aiService.responseAi(update.getMessage().getFrom().getId(), textRequest);
//            log.info("Сгенерирован ответ от AI: {}", response);
            sendMessage(update.getMessage().getChatId(), response);

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения пользователя: ", e);
        }
    }

    private boolean isMessageAndText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            log.info("Отправка сообщения в чат: {}. Сообщение: {}", chatId, text);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в чат {}: ", chatId, e);
        }
    }

    public String getUserMessage(Update update) {
        return update.getMessage().getText();
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}