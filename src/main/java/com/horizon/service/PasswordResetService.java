package com.horizon.service;

import com.horizon.entity.PasswordResetToken;
import com.horizon.entity.User;
import com.horizon.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final EmailService emailService;

    public void createPasswordResetTokenForUser(String email) {
        User user = userService.getByEmail(email);
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        // Email gönder
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        return !resetToken.isExpired();
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(newPassword);
        userService.save(user);

        // Token'ı sil
        tokenRepository.delete(resetToken);
    }
}