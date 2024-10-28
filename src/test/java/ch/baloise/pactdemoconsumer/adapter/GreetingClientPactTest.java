package ch.baloise.pactdemoconsumer.adapter;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.*;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "provider", pactVersion = PactSpecVersion.V3)
public class GreetingClientPactTest {

    @Pact(consumer = "consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .given("default")
                .uponReceiving("A request for a greeting message")
                .path("/greeting")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("{\"message\": \"Hello, World!\"}")
                .toPact();
    }

    @Test
    void testGetGreeting(MockServer mockServer) {
        // Given
        GreetingClient client = new GreetingClient(mockServer.getUrl());

        // When
        GreetingClient.Greeting greeting = client.getGreeting();

        // Then
        assertEquals("Hello, World!", greeting.getMessage());
    }
}
