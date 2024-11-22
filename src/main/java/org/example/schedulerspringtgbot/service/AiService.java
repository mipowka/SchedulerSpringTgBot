package org.example.schedulerspringtgbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schedulerspringtgbot.session.AiSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private static final String API_KEY = "";
    private static final String AIVERSION = "gpt-3.5-turbo";
    private static final String URL = "https://api.proxyapi.ru/openai/v1/chat/completions";

    private final Map<Long, AiSession> userSessions = new HashMap<>();

    public String responseAi(Long userId, String request) {
        AiSession session = getOrCreateSession(userId);
        session.addMessage(request);  // Добавляем новое сообщение в сессию

        logSessionInfo(userId, session);

        return getContent(sendRequest(session));
    }

    private AiSession getOrCreateSession(Long userId) {
        // Получаем текущую сессию пользователя, если она существует, или создаем новую
        return userSessions.computeIfAbsent(userId, id -> new AiSession(AIVERSION));
    }

    private void logSessionInfo(Long userId, AiSession session) {
        // Логируем сессии для отладки
        log.info("Сессия пользователя {}: {}", userId, session);
    }

    private String sendRequest(AiSession session) {
        HttpURLConnection connection = createConnection();

        String jsonRequest = createJsonRequest(session);
        sendJsonRequest(connection, jsonRequest);

        int responseCode = getResponseCode(connection);
        log.info("Получили код ответа: {}", responseCode);

        String responseText = getResponseText(connection);
        log.info("Получили JSON ответа: {}", responseText);

        return responseText;
    }

    private HttpURLConnection createConnection() {
        try {
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            log.info("Подключились к URL {}", url);
            return connection;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка в URL", e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при установке соединения", e);
        }
    }

    private String createJsonRequest(AiSession session) {
        // Формируем JSON для отправки, включая все предыдущие сообщения
        JSONArray messages = new JSONArray();
        session.getUserMessages().forEach(msg -> messages.put(createMessageObject(msg)));

        // Добавляем текущее сообщение
        String lastMessage = session.getUserMessages().get(session.getUserMessages().size() - 1);
        messages.put(createMessageObject(lastMessage));

        JSONObject body = new JSONObject();
        body.put("model", session.getModel());
        body.put("messages", messages);
        body.put("temperature", 1.1);

        return body.toString();
    }

    private JSONObject createMessageObject(String messageContent) {
        JSONObject messageObject = new JSONObject();
        messageObject.put("role", "user");
        messageObject.put("content", messageContent);
        return messageObject;
    }

    private void sendJsonRequest(HttpURLConnection connection, String jsonRequest) {
        byte[] jsonBytes = jsonRequest.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBytes, 0, jsonBytes.length);
            log.info("Отправили JSON: {}", jsonRequest);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке запроса", e);
        }
    }

    private int getResponseCode(HttpURLConnection connection) {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при получении кода ответа", e);
        }
    }

    private String getResponseText(HttpURLConnection connection) {
        try (InputStream is = connection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении ответа", e);
        }
    }

    public String getContent(String response) {
        JSONObject object = new JSONObject(response);
        if (object.has("choices")) {
            JSONArray choices = object.getJSONArray("choices");
            JSONObject choice = choices.getJSONObject(0);
            return choice.getJSONObject("message").getString("content");
        }
        return null;
    }
}