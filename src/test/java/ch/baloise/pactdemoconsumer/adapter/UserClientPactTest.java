package ch.baloise.pactdemoconsumer.adapter;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.*;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "userProvider", pactVersion = PactSpecVersion.V3)
public class UserClientPactTest {

    @Pact(consumer = "userConsumer")
    public RequestResponsePact getUserExistsPact(PactDslWithProvider builder) {
        return builder
                .given("User exists", Map.of("userId", "345"))
                .uponReceiving("A request to get user details when user exists")
                .path("/users/345")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .stringType("id", "345")
                        .stringType("name", "John Doe"))
                .toPact();
    }

    @Pact(consumer = "userConsumer")
    public RequestResponsePact getUserDoesNotExistPact(PactDslWithProvider builder) {
        return builder
                .given("User does not exist", Map.of("userId", "999"))
                .uponReceiving("A request to get user details when user does not exist")
                .path("/users/999")
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Pact(consumer = "userConsumer")
    public RequestResponsePact updateUserPact(PactDslWithProvider builder) {
        return builder
                .given("User exists and can be updated", Map.of("userId", "123"))
                .uponReceiving("A request to update user details")
                .path("/users/123")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .body(new PactDslJsonBody()
                        .nullValue("id") // Accepts null value for 'id'
                        .stringType("name", "Jane Doe"))
                .willRespondWith()
                .status(204)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getUserExistsPact")
    void testGetUserExists(MockServer mockServer) {
        UserClient client = new UserClient(mockServer.getUrl());
        UserClient.User user = client.getUser("345");
        assertNotNull(user);
        assertEquals("345", user.getId());
        assertEquals("John Doe", user.getName());
    }

    @Test
    @PactTestFor(pactMethod = "getUserDoesNotExistPact")
    void testGetUserDoesNotExist(MockServer mockServer) {
        UserClient client = new UserClient(mockServer.getUrl());
        UserClient.User user = client.getUser("999");
        assertNull(user);
    }

    @Test
    @PactTestFor(pactMethod = "updateUserPact")
    void testUpdateUser(MockServer mockServer) {
        UserClient client = new UserClient(mockServer.getUrl());
        UserClient.User user = new UserClient.User(null, "Jane Doe");
        client.updateUser("123", user);
        // Test passes if no exception is thrown
    }
}
