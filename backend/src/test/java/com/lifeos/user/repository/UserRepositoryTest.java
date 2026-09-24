package com.lifeos.user.repository;

import com.lifeos.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user and retrieve by email successfully")
    void shouldSaveAndFindByEmail() {
        User user = new User("Alice Walker", "alice@example.com", "$2a$12$hashedPasswordPlaceholder");
        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice Walker");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
        assertThat(userRepository.existsByEmail("alice@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException when saving duplicate email")
    void shouldThrowWhenDuplicateEmail() {
        User user1 = new User("User One", "unique@example.com", "hash1");
        userRepository.saveAndFlush(user1);

        User user2 = new User("User Two", "unique@example.com", "hash2");
        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should populate auditing timestamps automatically on save and update")
    void shouldPopulateAuditingTimestamps() throws InterruptedException {
        User user = new User("Audit User", "audit@example.com", "hash");
        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        var initialCreatedAt = savedUser.getCreatedAt();

        savedUser.setName("Updated Name");
        User updatedUser = userRepository.saveAndFlush(savedUser);

        assertThat(updatedUser.getCreatedAt()).isEqualTo(initialCreatedAt);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }
}
