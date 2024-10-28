package ch.baloise.pactdemoconsumer.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class UserClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public UserClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public User getUser(String userId) {
        try {
            return restTemplate.getForObject(baseUrl + "/users/" + userId, User.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    public void updateUser(String userId, User user) {
        restTemplate.put(baseUrl + "/users/" + userId, user);
    }

    @Getter
    public static class User {

        private String id;
        private String name;

        public User() {
        }

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and setters omitted for brevity
    }
}

