package com.pickdo.backend.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Integer currentExp = 0;

    public UserProfile(User user, String nickname) {
        this.user = user;
        this.nickname = nickname;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addExp(int exp) {
        this.currentExp += exp;
        // Level up logic can be added here later if needed
    }
}
