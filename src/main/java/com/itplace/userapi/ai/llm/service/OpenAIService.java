package com.itplace.userapi.ai.llm.service;

import com.itplace.userapi.ai.llm.entity.ChatHistory;
import com.itplace.userapi.ai.llm.repository.ChatHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
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
import org.springframework.beans.factory.annotation.Value;
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

    public String categorize(String userInput) {

        ChatClient chatClient = ChatClient.create(openAiChatModel);

        // 여기에 프롬프트
        SystemMessage systemMessage = new SystemMessage("""
                # 카테고리 추출 프롬프트
                
                ## 역할
                당신은 사용자의 자연어 입력을 분석하여 관련된 카테고리를 추출하는 AI 어시스턴트입니다.
                
                ## 사용 가능한 카테고리 목록
                - 경양식
                - 관광
                - 관광, 체험
                - 도서, 음반, 문구
                - 독서실
                - 루지
                - 미술관
                - 미용
                - 버거
                - 쇼핑
                - 슈퍼마켓
                - 복합문화
                - 상담
                - 식당
                - 심리
                - 아이스크림/빙수
                - 영화관
                - 자동차 정비소
                - 온천, 스파
                - 워터파크
                - 이탈리아 음식
                - 전시관
                - 제과
                - 치킨
                - 카페
                - 키즈카페, 실내놀이터
                - 타이어 소매업
                - 테마
                - 패밀리레스토랑
                - 편의점
                - 푸드코트
                - 피자
                - 학원
                - 호텔
                
                ## 추출 규칙
                1. 사용자의 입력을 분석하여 의도와 맥락을 파악하세요
                2. 위의 카테고리 목록에서 사용자의 요구사항과 가장 관련성이 높은 카테고리를 선택하세요
                3. 최대 3개까지의 카테고리를 선택할 수 있습니다
                4. 관련성이 높은 순서대로 배열하세요
                5. 반드시 JSON 형태로 응답하세요
                
                ## 응답 형식
                {
                  "categories": ["카테고리1", "카테고리2", "카테고리3"]
                }
                
                ## 예시
                사용자 입력: "아 덥다"
                {
                  "categories": ["아이스크림/빙수", "카페", "워터파크"]
                }
                
                사용자 입력: "놀고싶다"
                {
                  "categories": ["관광, 체험", "영화관", "워터파크"]
                }
                
                사용자 입력: "배고프다"
                {
                  "categories": ["식당", "패밀리레스토랑", "푸드코트"]
                }
                
                사용자 입력: "치킨 먹고싶다"
                {
                  "categories": ["치킨"]
                }
                
                ## 주의사항
                - 반드시 제공된 카테고리 목록에서만 선택하세요
                - 사용자의 감정이나 상황을 고려하여 적절한 카테고리를 추천하세요
                - 예시에 없는 응답은 LLM이 판단하여 어울리는 카테고리를 추천하세요.
                - 응답은 반드시 유효한 JSON 형태여야 합니다
                - 카테고리명은 정확히 목록과 일치해야 합니다
                """
        );

//        messages.add(0, systemMessage);

        UserMessage userMessage = new UserMessage(userInput);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        String content = chatClient.prompt(prompt)
                .call()
                .content();

//        saveChatHistory(userInput, userId, reason.getReason());

        return content;
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
