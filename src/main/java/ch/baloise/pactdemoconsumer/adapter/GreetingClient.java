package ch.baloise.pactdemoconsumer.adapter;

import org.springframework.web.client.RestTemplate;

public class GreetingClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public GreetingClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Greeting getGreeting() {
        return restTemplate.getForObject(baseUrl + "/greeting", Greeting.class);
    }

    public static class Greeting {
        private String message;

        public Greeting() {
        }

        public Greeting(String message) {
            this.message = message;
        }

        // Getter and Setter methods
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

