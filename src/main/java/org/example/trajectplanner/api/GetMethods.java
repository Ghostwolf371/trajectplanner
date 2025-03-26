package org.example.trajectplanner.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetMethods {
    private static final String BASE_URL = "https://trajectplannerapi.dulamari.com";

    public HttpResponse<String> getStudentByNumber(String studentNumber) {
        try {
            // Ensure student number has dashes instead of slashes
            String formattedStudentNumber = studentNumber.replace("/", "-");
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/students/" + formattedStudentNumber))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> getExams() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://trajectplannerapi.dulamari.com/exams"))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
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

    public HttpResponse<String> getCourses() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://trajectplannerapi.dulamari.com/courses"))
            .GET()
            .build();
        
        return HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getSemesters() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://trajectplannerapi.dulamari.com/semesters"))
            .GET()
            .build();
        
        return HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getStudents() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/students"))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

