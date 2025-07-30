package com.itplace.userapi.ai.llm.service;

import com.itplace.userapi.ai.llm.dto.RecommendReason;
import com.itplace.userapi.ai.llm.entity.ChatHistory;
import com.itplace.userapi.ai.llm.repository.ChatHistoryRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

    @Value("${spring.ai.openai.embedding.model}")
    private final String EMBEDDING_MODEL;

    @Value("${spring.ai.chat.categorizePrompt}")
    private Resource categorizePromptRes;

    @Value("${spring.ai.chat.reasonPrompt}")
    private Resource reasonPromptRes;

    private String categorizePrompt;
    private String reasonPrompt;


    @PostConstruct
    public void init() throws IOException {
        categorizePrompt = readPrompt(categorizePromptRes);
        reasonPrompt = readPrompt(reasonPromptRes);
    }

    private String readPrompt(Resource resource) throws IOException {
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public String generateReasons(String userInput, String category, List<String> partnerNames) {

        ChatClient chatClient = ChatClient.create(openAiChatModel);

        StringBuilder partnerList = new StringBuilder();
        for (String name : partnerNames) {
            partnerList.append("- ").append(name).append("\n");
        }

        String formattedPrompt = String.format(reasonPrompt, userInput, category, partnerList);

        return chatClient.prompt()
                .system(formattedPrompt)
                .options(ChatOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.7)
                        .build())
                .user(userInput)
                .call()
                .entity(RecommendReason.class)
                .getReason();
    }

    public String categorize(String userInput) {
        ChatClient chatClient = ChatClient.create(openAiChatModel);
        return chatClient.prompt()
                .system(categorizePrompt)
                .options(ChatOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.7)
                        .build())
                .user(userInput)
                .call()
                .content();
    }

    private void saveChatHistory(String userInput, Long userId, String response) {
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
                .model(EMBEDDING_MODEL)
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
