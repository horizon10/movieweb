package com.horizon.service;

import com.horizon.entity.ContactMessage;
import com.horizon.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }

    public void deleteMessage(Long id) {
        contactMessageRepository.deleteById(id);
    }
    public ContactMessage saveMessage(ContactMessage message) {
        return contactMessageRepository.save(message);
    }
}
