package ch.baloise.pactdemoconsumer.controller;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.baloise.pactdemoconsumer.model.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "pact-demo-provider", pactVersion = PactSpecVersion.V3)
public class UserClientControllerPactTest {
  private UserClientController userClientController;

  private static final String USER_PATH = "/users";

  private static final User USER_1 = new User("1", "John Doe");
  private static final User USER_2 = new User("2", "Jane Doe");

  @Nested
  class getUserTests {
    @Pact(consumer = "pact-demo-consumer")
    public RequestResponsePact createGetUserPact(PactDslWithProvider builder) {
      return builder
          .given("User exists", Map.of("userId", USER_1.getId()))
          .uponReceiving("A request to get user details when user exists")
          .path(USER_PATH + "/" + USER_1.getId())
          .method("GET")
          .willRespondWith()
          .status(200)
          .body(new PactDslJsonBody()
              .stringType("id", USER_1.getId())
              .stringType("name", USER_1.getName()))
          .toPact();
    }

    @Pact(consumer = "pact-demo-consumer")
    public RequestResponsePact createGetUserDoesNotExistPact(PactDslWithProvider builder) {
      return builder
          .given("User does not exist", Map.of("userId", USER_1.getId()))
          .uponReceiving("A request to get user details when user does not exist")
          .path(USER_PATH + "/" + USER_1.getId())
          .method("GET")
          .willRespondWith()
          .status(404)
          .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createGetUserPact")
    void testGetExists(MockServer mockServer) {
      userClientController = new UserClientController(mockServer.getUrl());

      User user = userClientController.get("1");
      assertUserEquals(user, USER_1);
    }

    @Test
    @PactTestFor(pactMethod = "createGetUserDoesNotExistPact")
    void testGetUserDoesNotExist(MockServer mockServer) {
      userClientController = new UserClientController(mockServer.getUrl());
      User user = userClientController.get("1");
      assertNull(user);
    }
  }

  @Nested
  class createUserTests {
    @Pact(consumer = "pact-demo-consumer")
    public RequestResponsePact createCreateUserPact(PactDslWithProvider builder) {
      return builder
          .given("No User exists")
          .uponReceiving("A request to create a user")
          .path(USER_PATH)
          .method("POST")
          .headers("Content-Type", "application/json")
          .body(new PactDslJsonBody()
              .nullValue("id")
              .stringType("name", USER_1.getName()))
          .willRespondWith()
          .status(200)
          .body(new PactDslJsonBody()
              .stringType("id", USER_1.getId())
              .stringType("name", USER_1.getName()))
          .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createCreateUserPact")
    void testCreateUser(MockServer mockServer) {
      userClientController = new UserClientController(mockServer.getUrl());

      userClientController.create(new User(null, USER_1.getName()));
    }
  }

  @Nested
  class updateUserTests {
    @Pact(consumer = "pact-demo-consumer")
    public RequestResponsePact createUpdateUserPact(PactDslWithProvider builder) {
      return builder
          .given("User exists and can be updated", Map.of("userId", USER_1.getId()))
          .uponReceiving("A request to update user details")
          .path(USER_PATH + "/" + USER_1.getId())
          .method("PUT")
          .headers("Content-Type", "application/json")
          .body(new PactDslJsonBody()
              .stringType("id", USER_1.getId()) // Accepts null value for 'id'
              .stringType("name", USER_2.getName()))
          .willRespondWith()
          .status(204)
          .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createUpdateUserPact")
    void testUpdateUser(MockServer mockServer) {
      userClientController = new UserClientController(mockServer.getUrl());

      User updatedUser = USER_1;
      updatedUser.setName(USER_2.getName());
      userClientController.update(updatedUser.getId(), updatedUser);
    }
  }

  @Nested
  class deleteUserTests {
    @Pact(consumer = "pact-demo-consumer")
    public RequestResponsePact createDeleteUserPact(PactDslWithProvider builder) {
      return builder
          .given("User exists and can be deleted", Map.of("userId", USER_1.getId()))
          .uponReceiving("A request to delete a user")
          .path(USER_PATH + "/" + USER_1.getId())
          .method("DELETE")
          .willRespondWith()
          .status(204)
          .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createDeleteUserPact")
    void testDeleteUser(MockServer mockServer) {
      userClientController = new UserClientController(mockServer.getUrl());

      userClientController.delete("1");
    }
  }

  private void assertUserEquals(User actual, User expected) {
    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(expected.getId());
    assertThat(actual.getName()).isEqualTo(expected.getName());
  }
}
