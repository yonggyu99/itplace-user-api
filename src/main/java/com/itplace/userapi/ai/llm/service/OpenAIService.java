package com.itplace.userapi.ai.llm.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.itplace.userapi.ai.llm.dto.RecommendReason;
import com.itplace.userapi.ai.llm.entity.ChatHistory;
import com.itplace.userapi.ai.llm.repository.ChatHistoryRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
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
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource;
import org.springframework.ai.openai.OpenAiChatOptions;
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

    private String categorizePrompt;

    @PostConstruct
    public void init() throws IOException {
        categorizePrompt = readPrompt(categorizePromptRes);
    }

    private String readPrompt(Resource resource) throws IOException {
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    public String generateReasons(
            String userInput, String category, List<String> partnerNames) {

        ChatClient chatClient = ChatClient.create(openAiChatModel);

        StringBuilder partnerList = new StringBuilder();
        for (String name : partnerNames) {
            partnerList.append("- ").append(name).append("\n");
        }

        SystemMessage systemMessage = new SystemMessage(String.format("""
                너는 사용자 질문에 맞춰 제휴처별로 왜 그 장소가 어울리는지를 설명해주는 안내 시스템이야.
                
                [사용자 질문]
                %s
                
                [관련 카테고리]
                %s
                
                [추천 제휴처 목록]
                %s
                
                위 정보를 바탕으로, 사용자에게 어울리는 제휴처들이라는 것을 자연스럽고 설득력 있게 설명해줘.
                결과는 반드시 아래와 같은 JSON 형식으로, reason 필드 하나만 포함해서 응답해:
                
                {
                     "reason": "..."
                }                            
                """, userInput, category, partnerList.toString()));

        Prompt prompt = new Prompt(
                List.of(systemMessage, new UserMessage(userInput)),
                OpenAiChatOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.7)
                        .build()
        );

        RecommendReason result = chatClient.prompt(prompt)
                .call()
                .entity(RecommendReason.class);

        return result.getReason();
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
