package org.example.chatgptclonebackend.controllers;


import org.example.chatgptclonebackend.outputs.Carte;
import org.example.chatgptclonebackend.outputs.ListeVoiture;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
public class AiAgentController {
    private ChatClient chatClient;
    private OpenAiImageModel openAiImageModel;
    public AiAgentController(ChatClient.Builder builder, ChatMemory chatMemory, OpenAiImageModel openAiImageModel) {
        this.chatClient = builder
                //.defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.openAiImageModel=openAiImageModel;
    }
    // one shut example
    @GetMapping("/expchat")
    public String chat(String querry) {
        List<Message> exemples= List.of(
                new UserMessage("6+5"),
                new AssistantMessage("le resultat est 11")
        );
        return chatClient.prompt()
                .system("ercit en majiscule")
                .messages(exemples)
                .user(querry)
                .call().content();
    }
    // reponse en mode stream
    @GetMapping(value = "/stream",  produces= MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> stream(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("Query must not be empty"));
        }
        return chatClient.prompt()
                .user(query).stream().content();
    }
    // reponse direct
    @GetMapping("/nostream")
    public String nostream(String query) {
        return chatClient.prompt()
                .user(query).call().content();
    }
    //reponse structurée
    @GetMapping("/strucchat")
    public ListeVoiture strucChat(String querry) {
        String systemMessage= "vous etes un specialiste dans les voitures";
        return chatClient.prompt()
                .system(systemMessage).user(querry)
                .call().entity(ListeVoiture.class);
    }
    @Value("classpath:images/image1.jpeg")
    private Resource image;

    @GetMapping("/descImg")
    public Carte describeImg() {
        return chatClient.prompt()
                .system("donne moi une description de l'image")
                .user(u->u.text("Décrire cette image")
                        .media(MediaType.IMAGE_JPEG,image))
                .call().entity(Carte.class);
    }
    @PostMapping(value = "/askImg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String askImg(String querry, @RequestParam(name = "image") MultipartFile image) throws IOException {
        byte[] bytes=image.getBytes();
        return chatClient.prompt()
                .system("répond à la question à propos de l'image fournie")
                .user(u->u.text(querry)
                        .media(MediaType.IMAGE_PNG, new ByteArrayResource(bytes)))
                .call().content();
    }
    @GetMapping("/generate")
    public String generate(String querry) {
        ImageOptions imageOptions= OpenAiImageOptions
                .builder()
                .quality("hd").model("dall-e-3")
                .build();
        ImagePrompt imagePrompt= new ImagePrompt(querry, imageOptions);
        String url = openAiImageModel.call(imagePrompt).getResult().getOutput().getUrl();
        return url;
    }
}
