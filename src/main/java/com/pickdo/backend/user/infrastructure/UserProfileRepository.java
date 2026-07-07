package com.pickdo.backend.user.infrastructure;

import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByUser(User user);
}
