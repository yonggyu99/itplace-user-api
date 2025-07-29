package com.itplace.userapi.ai.llm.service;

import com.itplace.userapi.ai.llm.entity.ChatHistory;
import com.itplace.userapi.ai.llm.repository.ChatHistoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public List<ChatHistory> readAllChats(String userId) {
        return chatHistoryRepository.findByUserIdOrderByCreatedAtAsc(userId);
    }
}
