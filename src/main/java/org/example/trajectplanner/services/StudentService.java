package org.example.trajectplanner.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StudentService {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> create(String requestBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}