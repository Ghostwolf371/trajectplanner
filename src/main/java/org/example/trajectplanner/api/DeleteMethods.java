package org.example.trajectplanner.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteMethods {
    private static final String BASE_URL = "https://trajectplannerapi.dulamari.com";

    public HttpResponse<String> deleteTentamen(String tentamenId) {
        try {
            String url = BASE_URL + "/exams/" + tentamenId;
            String jsonBody = String.format("{\"exam_id\": %s}", tentamenId);
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
