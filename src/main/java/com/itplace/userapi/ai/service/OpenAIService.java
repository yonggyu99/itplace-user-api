package com.itplace.userapi.ai.service;

import com.itplace.userapi.ai.dto.RecommendReason;
import com.itplace.userapi.ai.entity.ChatHistory;
import com.itplace.userapi.ai.repository.ChatHistoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAiChatModel openAiChatModel;
    private final OpenAiEmbeddingModel openAiEmbeddingModel;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    public String categorize(String userInput) {

        ChatClient chatClient = ChatClient.create(openAiChatModel);

        String userId = "test_2";
        List<ChatHistory> history = chatHistoryRepository.findByUserIdOrderByCreatedAtAsc(userId);

        List<Message> messages = history.stream()
                .map(chat -> {
                    if (chat.getType() == MessageType.USER) {
                        return new UserMessage(chat.getContent());
                    } else {
                        return new AssistantMessage(chat.getContent());
                    }
                }).collect(Collectors.toList());

        messages.add(new UserMessage(userInput));

        // 여기에 프롬프트
        SystemMessage systemMessage = new SystemMessage("""
                You are a keyword analysis system. Your primary goal is to accurately extract all relevant business categories from a user's request.
                
                [Rules]
                1. Analyze the user's intent and generate a concise reason for the recommendation.
                2. The output MUST BE a valid JSON object with a single key named "reason".
                
                [Category List]
                치킨, 피자, 버거, 카페, 제과, 아이스크림/빙수, 쇼핑, 영화관, 편의점, 미용, 심리
                
                [Example]
                User Request: "영화 보고 나와서 간단하게 커피 마실 곳 추천해줘."
                JSON Output:
                {
                  "reason": "사용자는 영화관람 후 카페 방문을 원하고 있습니다."
                }
                
                [Actual Task]
                User Request: "{userInput}"
                JSON Output: ""
                """
        );

        messages.add(0, systemMessage);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .build();

        Prompt prompt = new Prompt(messages, options);

        RecommendReason reason = chatClient.prompt(prompt)
                .call()
                .entity(RecommendReason.class);

        saveChatHistory(userInput, userId, reason.getReason());

        return reason.getReason();
    }

    private void saveChatHistory(String userInput, String userId, String response) {
        ChatHistory userChatHistory = new ChatHistory();
        userChatHistory.setUserId(userId);
        userChatHistory.setType(MessageType.USER);
        userChatHistory.setContent(userInput);

        ChatHistory assistantChatHistory = new ChatHistory();
        assistantChatHistory.setUserId(userId);
        assistantChatHistory.setType(MessageType.ASSISTANT);
        assistantChatHistory.setContent(response);
        chatHistoryRepository.saveAll(List.of(userChatHistory, assistantChatHistory));
    }

    public List<float[]> generateEmbedding(List<String> texts) {

        // 옵션
        EmbeddingOptions embeddingOptions = OpenAiEmbeddingOptions.builder()
                .model("text-embedding-3-large")
                .build();

        // 프롬프트
        EmbeddingRequest prompt = new EmbeddingRequest(texts, embeddingOptions);

        // 요청 및 응답
        EmbeddingResponse response = openAiEmbeddingModel.call(prompt);
        return response.getResults().stream()
                .map(Embedding::getOutput)
                .toList();
    }

    // 1. 사용자 메시지를 저장하는 메소드를 분리하고 @Transactional 추가
    @Transactional
    @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void saveUserMessage(String userId, String message) {
        ChatMemory chatMemory = buildChatMemory();
        chatMemory.add(userId, new UserMessage(message));
    }

    // 2. AI 응답을 저장하는 메소드를 분리하고 @Transactional 추가
    @Transactional
    @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void saveAssistantMessage(String userId, String message) {
        ChatMemory chatMemory = buildChatMemory();
        chatMemory.add(userId, new AssistantMessage(message));
    }

    // ChatMemory 객체를 생성하는 헬퍼 메소드
    private ChatMemory buildChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10) // 필요에 따라 조절
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }

}
