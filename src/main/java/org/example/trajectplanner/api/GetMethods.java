package org.example.trajectplanner.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetMethods {

    public HttpResponse<String> getExams() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://trajectplannerapi.dulamari.com/exams"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> postExamById(String id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://trajectplannerapi.dulamari.com/exams/" + id))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> getScores() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://trajectplannerapi.dulamari.com/scores"))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> getScoresByStudentNumber(String studentNumber) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://trajectplannerapi.dulamari.com/scores/" + studentNumber))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> getExamById(String tentamenId) {
        try {
            String url = "https://trajectplannerapi.dulamari.com/exams/" + tentamenId;
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response;
        } catch (Exception e) {
            System.err.println("Error in getExamById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> getCourseById(String courseId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://trajectplannerapi.dulamari.com/courses/" + courseId))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Error in getCourseById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> getScoreById(String scoreId) {
        try {
            if (scoreId == null || scoreId.trim().isEmpty()) {
                return null;
            }

            String url = "https://trajectplannerapi.dulamari.com/scores/" + scoreId;
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

