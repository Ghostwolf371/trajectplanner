package org.example.trajectplanner.API;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ApiClient {
    private static final String BASE_URL = "https://trajectplannerapi.dulamari.com";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> get(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + endpoint))
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpResponse<String> post(String endpoint, String body) {
        return sendRequest(endpoint, "POST", body);
    }

    public static HttpResponse<String> put(String endpoint, String body) {
        return sendRequest(endpoint, "PUT", body);
    }

    public static HttpResponse<String> delete(String endpoint, String body) {
        return sendRequest(endpoint, "DELETE", body);
    }

    private static HttpResponse<String> sendRequest(String endpoint, String method, String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}