package com.gusten.dbfrontend;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EmployeeService {
    private static final String BASE_URL = "http://172.26.200.64:3000";
    private final HttpClient client;

    public EmployeeService() {
        this.client = HttpClient.newHttpClient();
    }

    public void addEmployee(String name) throws Exception {
        String jsonPayload = new ObjectMapper().writeValueAsString(new EmployeePayload(name));
        System.out.println("Sending payload: " + jsonPayload);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/employees"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            System.err.println("Failed to add employee with status code: " + response.statusCode());
            throw new Exception("Failed to add employee: " + response.body());
        }

    }

    public static class EmployeePayload {
        private String name;

        public EmployeePayload(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
