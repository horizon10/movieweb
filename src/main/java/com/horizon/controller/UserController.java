package com.horizon.controller;


import com.horizon.dto.UserDto;
import com.horizon.dto.UserResponse;
import com.horizon.entity.User;
import com.horizon.repository.UserRepository;
import com.horizon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // React dev server için
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        List<UserDto> users=userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        try {
            User user = userService.getByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody User user) {

        try {
            User updatedUser = userService.update(id, user);
            return ResponseEntity.ok(UserResponse.builder()
                    .id(updatedUser.getId())
                    .username(updatedUser.getUsername())
                    .email(updatedUser.getEmail())
                    .image(updatedUser.getImage())
                    .role(updatedUser.getRole().name())
                    .build());
        } catch (IllegalArgumentException e) {
            // Username veya email zaten var gibi durumlar için
            return ResponseEntity.badRequest()
                    .body(UserResponse.builder()
                            .build()); // Hata mesajını frontend'e iletmek için uygun bir yapı kullanın
        } catch (RuntimeException e) {
            // User bulunamadı gibi durumlar için
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Username kontrolü için endpoint
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(
            @PathVariable String username,
            @RequestParam(required = false) Long currentUserId) {

        // Mevcut kullanıcının kendi adını kontrol etmesine izin ver
        Optional<User> existingUser = userRepository.findByUsername(username);
        boolean available = !existingUser.isPresent() ||
                (currentUserId != null && existingUser.get().getId().equals(currentUserId));

        return ResponseEntity.ok(Map.of(
                "available", available,
                "message", available ? "Username available" : "Username already taken"
        ));
    }

    //şifremi unuttum için


}