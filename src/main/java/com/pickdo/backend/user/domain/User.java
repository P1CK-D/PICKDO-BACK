package com.pickdo.backend.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Integer currentExp = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(String email, String googleId, String nickname) {
        this.email = email;
        this.googleId = googleId;
        this.nickname = nickname;
    }

    public void linkGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
