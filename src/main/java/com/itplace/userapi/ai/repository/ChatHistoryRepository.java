package com.itplace.userapi.ai.repository;

import com.itplace.userapi.ai.entity.ChatHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserIdOrderByCreatedAtAsc(String userId);
}
