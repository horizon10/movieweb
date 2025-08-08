package com.horizon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {

    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable Long id) {
        // yorum silme işlemi burada yapılır
        return "Yorum silindi. ID: " + id;
    }
}
