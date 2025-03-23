package org.example.trajectplanner.API;

import java.net.http.HttpResponse;

public class ScoreService {
    public static HttpResponse<String> getAll() {
        return ApiClient.get("/scores");
    }

    public static HttpResponse<String> getById(String scoreId) {
        return ApiClient.get("/scores/" + scoreId);
    }

    public static HttpResponse<String> create(String scoreData) {
        return ApiClient.post("/scores", scoreData);
    }

    public static HttpResponse<String> update(String scoreData) {
        return ApiClient.put("/scores", scoreData);
    }

    public static HttpResponse<String> delete(String scoreId) {
        String requestBody = String.format("{\"score_id\": %s}", scoreId);
        return ApiClient.delete("/scores/" + scoreId, requestBody);
    }
}
