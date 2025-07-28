package com.itplace.userapi.ai.controller;

import com.itplace.userapi.ai.entity.ChatHistory;
import com.itplace.userapi.ai.service.ChatHistoryService;
import com.itplace.userapi.ai.service.OpenAIService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final OpenAIService openAIService;
    private final ChatHistoryService chatHistoryService;

    @GetMapping("/chat")
    public String chat() {
        return "/chat.html";
    }

    @ResponseBody
    @PostMapping("/categorize")
    public String categorize(@RequestBody Map<String, String> body) {
        return openAIService.categorize(body.get("text"));
    }

    @ResponseBody
    @PostMapping("/chat/history/{userid}")
    public List<ChatHistory> getChatHistory(@PathVariable("userid") String userId) {
        return chatHistoryService.readAllChats(userId);
    }

}