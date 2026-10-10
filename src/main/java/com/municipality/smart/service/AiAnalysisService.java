package com.municipality.smart.service;

import com.municipality.smart.dto.AiAnalysisResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiAnalysisService {

    private final GeminiAiClient geminiAiClient;
    private final MockAiService mockAiService;

    @Autowired
    public AiAnalysisService(GeminiAiClient geminiAiClient, MockAiService mockAiService) {
        this.geminiAiClient = geminiAiClient;
        this.mockAiService = mockAiService;
    }

    public AiAnalysisResultDto analyzeIssue(String description, String imagePath, String filename) {
        // Attempt Real Gemini AI first if configured
        if (geminiAiClient.isConfigured()) {
            AiAnalysisResultDto geminiResult = geminiAiClient.analyze(description, imagePath);
            if (geminiResult != null) {
                return geminiResult;
            }
        }

        // Graceful fallback to Mock Civic Rule Engine
        return mockAiService.analyze(description, filename);
    }
}
