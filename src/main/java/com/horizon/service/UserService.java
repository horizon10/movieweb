package com.horizon.service;

import com.horizon.dto.CommentAdminDTO;
import com.horizon.dto.UserDto;
import com.horizon.entity.Role;
import com.horizon.entity.User;
import com.horizon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email bulunamadı: " + email));
    }
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(c -> new UserDto(
                        c.getId(),
                        c.getEmail(),
                        c.getUsername(),
                        c.getPassword(),
                        c.getImage(),
                        c.getRole()
                ))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User update(Long id, User updatedUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Username benzersizlik kontrolü - sadece değiştirildiyse kontrol et
        if (!existing.getUsername().equals(updatedUser.getUsername())) {
            Optional<User> userWithSameUsername = userRepository.findByUsername(updatedUser.getUsername());
            if (userWithSameUsername.isPresent()&& !userWithSameUsername.get().getId().equals(id)) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        // Email benzersizlik kontrolü - eğer email unique ise
        if (!existing.getEmail().equals(updatedUser.getEmail())) {
            Optional<User> userWithSameEmail = userRepository.findByEmail(updatedUser.getEmail());
            if (userWithSameEmail.isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        // Resim boyutu kontrolü
        if (updatedUser.getImage() != null) {
            // Base64 string'in gerçek boyutunu hesapla (base64 padding'i hesaba kat)
            String base64Data = updatedUser.getImage();
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1]; // "data:image/jpeg;base64," kısmını çıkar
            }

            // Base64'ün gerçek boyutunu hesapla
            int actualSize = (base64Data.length() * 3) / 4;
            if (actualSize > 5 * 1024 * 1024) { // 5MB
                throw new IllegalArgumentException("Image size too large. Maximum 5MB allowed.");
            }
        }

        // Güncelleme işlemleri
        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());

        // Resim güncellemesi
        if (updatedUser.getImage() != null) {
            existing.setImage(updatedUser.getImage());
        }

        // Şifre sadece sağlanmışsa güncelle
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existing);
    }

    public User updateRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(newRole);
        return userRepository.save(user);
    }

}