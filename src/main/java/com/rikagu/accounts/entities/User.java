package com.rikagu.accounts.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @Column
    private boolean verified = false;
    @Column
    private String verificationCode;

    @PrePersist
    public void prePersist() {
        verificationCode = generateRandom6numbers();
    }

    private String generateRandom6numbers() {
        final int min = 100000;
        final int max = 999999;
        return String.valueOf((int) (Math.random() * (max - min + 1) + min));
    }
}
