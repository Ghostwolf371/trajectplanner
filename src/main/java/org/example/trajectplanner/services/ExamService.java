package org.example.trajectplanner.services;

import java.net.http.HttpResponse;

import org.example.trajectplanner.api.ApiClient;

public class ExamService {
    public static HttpResponse<String> getAll() {
        return ApiClient.get("/exams");
    }

    public static HttpResponse<String> getById(String examId) {
        return ApiClient.get("/exams/" + examId);
    }

    public static HttpResponse<String> create(String examData) {
        return ApiClient.post("/exams", examData);
    }

    public static HttpResponse<String> update(String examData) {
        return ApiClient.put("/exams", examData);
    }

    public static HttpResponse<String> delete(String examId) {
        String body = String.format("{\"exam_id\": %s}", examId);
        return ApiClient.delete("/exams/" + examId, body);
    }
}
