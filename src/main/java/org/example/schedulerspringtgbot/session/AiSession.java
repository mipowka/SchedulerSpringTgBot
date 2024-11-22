package org.example.schedulerspringtgbot.session;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiSession {

    private List<String> userMessages = new ArrayList<>();
    private String model;

    public AiSession(String model) {
        this.model = model;
    }

    public void addMessage(String message) {
        userMessages.add(message);
    }
}
