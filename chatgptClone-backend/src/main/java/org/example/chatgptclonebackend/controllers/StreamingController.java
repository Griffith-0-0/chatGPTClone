//package org.example.chatgptclonebackend.controllers;
//
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//
//@RestController
//@CrossOrigin(origins = "*")
//public class StreamingController {
//    private ChatClient chatClient;
//
//    public StreamingController(ChatClient.Builder builder, ChatMemory memory) {
//        this.chatClient = builder
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
//                .build();
//    }
//
//    @GetMapping(value = "/stream",  produces=MediaType.TEXT_PLAIN_VALUE)
//    public Flux<String> stream(String query) {
//        if (query == null || query.trim().isEmpty()) {
//            return Flux.error(new IllegalArgumentException("Query must not be empty"));
//        }
//        return chatClient.prompt()
//                .user(query).stream().content();
//    }
//    @GetMapping("/nostream")
//    public String nostream(String query) {
//        return chatClient.prompt()
//                .user(query).call().content();
//    }
//}
