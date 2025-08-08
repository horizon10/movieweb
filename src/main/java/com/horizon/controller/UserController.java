package com.horizon.controller;


import com.horizon.dto.UserResponse;
import com.horizon.entity.User;
import com.horizon.repository.UserRepository;
import com.horizon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



import java.util.List;
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
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getByUsername(username));
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
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        // Bu endpoint ile frontend'de username'in mevcut olup olmadığını kontrol edebilirsiniz
        try {
            userService.getByUsername(username);
            return ResponseEntity.ok(true); // Username mevcut
        } catch (Exception e) {
            return ResponseEntity.ok(false); // Username mevcut değil
        }
    }

    //şifremi unuttum için


}