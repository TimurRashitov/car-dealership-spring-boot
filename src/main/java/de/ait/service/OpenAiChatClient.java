package de.ait.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OpenAiChatClient {

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;



}
