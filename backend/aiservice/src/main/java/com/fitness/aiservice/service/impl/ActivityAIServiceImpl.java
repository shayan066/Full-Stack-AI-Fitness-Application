package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.entity.Activity;
import com.fitness.aiservice.model.entity.Recommendation;
import com.fitness.aiservice.service.ActivityAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ActivityAIServiceImpl implements ActivityAIService {

    private final GeminiServiceImpl geminiServiceImpl;


    public ActivityAIServiceImpl(GeminiServiceImpl geminiServiceImpl) {
        this.geminiServiceImpl = geminiServiceImpl;
    }

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiServiceImpl.getAnswer(prompt);
        log.info("Response from AI: {}", aiResponse);
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootnode =  mapper.readTree(aiResponse);
            JsonNode textNode = rootnode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            log.info("PARSE RESPONSE FROM AI: {}", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "OverAll:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");
            addAnalysisSection(fullAnalysis, analysisNode, "duration", "Duration:");
            addAnalysisSection(fullAnalysis, analysisNode, "intensity", "Intensity:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getActivityType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        }catch (Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getActivityType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Collections.singletonList("Always warm up before exercise"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(safe -> {
                String point = safe.path("point").asText();
                safety.add(String.format("%s", point));
            });
        }
        return safety.isEmpty() ? Collections.singletonList("No specific safety provided"): safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions provided") : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String recommendation = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, recommendation));
            });
        }
        return improvements.isEmpty() ? Collections.singletonList("No specific improvements provided") : improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String value) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(value)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
            You are a professional fitness coach and data analyst.

            Analyze the following fitness activity and return ONLY a valid JSON response.
            DO NOT include any explanation, markdown, or extra text outside JSON.

            JSON FORMAT (STRICT):
            {
                "analysis":{
                    "overall":"string",
                    "pace":"string",
                    "heartRate":"string",
                    "caloriesBurned":"string",
                    "duration":"string",
                    "intensity":"string"
                },
                "improvements":[
                    {
                        "area":"string",
                        "recommendation":"string"
                    }
                ],
                "suggestions":[
                    {
                        "workout":"string",
                        "description":"string"
                    }
                ],
                "safety":[
                    {
                        "point":"string"
                    }
                ],
                "summaryScore": number
            }

            RULES:
            - Response must be strictly valid JSON
            - No text outside JSON
            - Keep recommendations practical and actionable
            - Use Additional Metrics if available (heart rate, distance, steps, etc.)
            - If any data is missing, infer intelligently but do not mention missing data
            - Summary score should be between 1–10

            ACTIVITY DATA:
            - User: %s
            - Activity Type: %s
            - Duration: %d minutes
            - Calories Burned: %d
            - Start Time: %s
            - Additional Metrics: %s

            Focus on:
            - Performance quality
            - Efficiency
            - Improvement areas
            - Next workout plan
            - Injury prevention
            """,

                activity.getFirstAndLastName(),
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics()
        );
    }
}
