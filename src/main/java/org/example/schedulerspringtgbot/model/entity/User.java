package org.example.schedulerspringtgbot.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.schedulerspringtgbot.model.enums.Companion;


@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private Long chatId;


    @Enumerated(EnumType.STRING)
    Companion companion = Companion.FRIEND;


}
