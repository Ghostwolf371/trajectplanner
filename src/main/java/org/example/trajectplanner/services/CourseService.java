package org.example.trajectplanner.services;

import java.net.http.HttpResponse;
import org.example.trajectplanner.api.ApiClient;

public class CourseService {
    /**
     * Get all courses
     * @return List of all courses in the SE program
     */
    public static HttpResponse<String> getAll() {
        return ApiClient.get("/courses");
    }

    /**
     * Get course by ID or course code
     * @param identifier The course ID or course code
     * @return Course information
     */
    public static HttpResponse<String> getByIdOrCode(String identifier) {
        return ApiClient.get("/courses/" + identifier);
    }
}
