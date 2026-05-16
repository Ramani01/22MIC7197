package com.evaluation.service;

import com.evaluation.dto.LogRequestDto;
import com.evaluation.model.LogLevel;
import com.evaluation.model.LogPackage;
import com.evaluation.model.LogStack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoggingService {

    private final RestTemplate restTemplate;
    
    @Value("${external.api.base-url}")
    private String baseUrl;

    @Value("${external.api.bearer-token}")
    private String bearerToken;

    public LoggingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void log(LogStack stack, LogLevel level, LogPackage packageName, String message) {
        try {
            LogRequestDto requestDto = LogRequestDto.builder()
                    .stack(stack)
                    .level(level)
                    .packageName(packageName)
                    .message(message)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

            HttpEntity<LogRequestDto> request = new HttpEntity<>(requestDto, headers);
            String url = baseUrl + "/logs";
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("LOG [" + level + "] " + packageName + ": " + message);
        } catch (Exception e) {
            System.err.println("Failed to send log to external service: " + e.getMessage());
        }
    }
}
