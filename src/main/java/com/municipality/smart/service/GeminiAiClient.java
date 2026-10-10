package com.municipality.smart.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.municipality.smart.dto.AiAnalysisResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@Service
public class GeminiAiClient {

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.trim().equalsIgnoreCase("YOUR_GEMINI_API_KEY");
    }

    public AiAnalysisResultDto analyze(String description, String imagePath) {
        if (!isConfigured()) {
            return null;
        }

        try {
            String promptText = """
                Analyze this civic issue report. You are an expert Smart Municipality AI agent.
                Classify the issue based on description and image.
                Available categories: Streetlight, Pothole, Garbage, Water Leakage, Drainage, Road Damage, Traffic Signal, Public Infrastructure Damage, Other.
                Available severity levels: LOW, MEDIUM, HIGH, CRITICAL.
                Department mappings:
                - Streetlight -> Electrical Department
                - Pothole / Road Damage -> Roads Department
                - Garbage -> Sanitation Department
                - Water Leakage -> Water Supply Department
                - Drainage -> Drainage Department
                - Traffic Signal -> Traffic/Transport Department
                - Public Infrastructure Damage -> Public Works Department
                - Other -> General Administration

                Return ONLY valid JSON (no markdown formatting, no backticks) with this structure:
                {
                  "category": "Streetlight",
                  "severity": "HIGH",
                  "department": "Electrical Department",
                  "confidence": 92,
                  "reasoning": "Detailed technical explanation of classification and urgency."
                }
                
                Citizen Description: "%s"
                """.formatted(description != null ? description : "");

            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(Map.of("text", promptText));

            // Attach image inline if present and exists
            if (imagePath != null) {
                File file = new File(imagePath);
                if (file.exists() && file.length() < 10 * 1024 * 1024) {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String mimeType = Files.probeContentType(file.toPath());
                    if (mimeType == null) mimeType = "image/jpeg";
                    String base64Data = Base64.getEncoder().encodeToString(bytes);

                    Map<String, Object> inlineData = Map.of(
                            "mime_type", mimeType,
                            "data", base64Data
                    );
                    parts.add(Map.of("inline_data", inlineData));
                }
            }

            Map<String, Object> contentMap = Map.of("parts", parts);
            Map<String, Object> requestBody = Map.of("contents", List.of(contentMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = apiUrl + "?key=" + apiKey.trim();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    String rawText = candidates.get(0).path("content").path("parts").get(0).path("text").asText("");
                    // Strip potential markdown fences
                    String cleanedJson = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
                    JsonNode resultJson = objectMapper.readTree(cleanedJson);

                    AiAnalysisResultDto dto = new AiAnalysisResultDto();
                    dto.setCategory(resultJson.path("category").asText("Other"));
                    dto.setSeverity(resultJson.path("severity").asText("MEDIUM"));
                    dto.setDepartment(resultJson.path("department").asText("General Administration"));
                    dto.setConfidence(resultJson.path("confidence").asInt(90));
                    dto.setReasoning(resultJson.path("reasoning").asText("Analyzed by Gemini 1.5 Multimodal AI"));
                    dto.setAnalysisSource("GEMINI_AI");
                    return dto;
                }
            }
        } catch (Exception e) {
            System.err.println("Gemini API call failed or timed out: " + e.getMessage());
        }

        return null;
    }
}
