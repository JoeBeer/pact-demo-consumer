package ch.baloise.pactdemoconsumer.controller;

import ch.baloise.pactdemoconsumer.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserClientControllerTest {

  @Mock
  private RestTemplate restTemplate;

  private UserClientController userClientController;

  private final String baseUrl = "http://localhost:8080";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userClientController = new UserClientController(baseUrl, restTemplate);
  }

  @Test
  void testGetUserSuccess() {
    String userId = "123";
    User mockUser = new User(); // Setze die Eigenschaften des Mock-Users hier
    when(restTemplate.getForObject(eq(baseUrl + "/users/" + userId), eq(User.class))).thenReturn(mockUser);

    User user = userClientController.get(userId);

    assertNotNull(user);
    assertEquals(mockUser, user);
    verify(restTemplate).getForObject(baseUrl + "/users/" + userId, User.class);
  }

  @Test
  void testGetUserNotFound() {
    String userId = "456";
    when(restTemplate.getForObject(eq(baseUrl + "/users/" + userId), eq(User.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    // Verwende assertThrows, um zu prüfen, dass die allgemeine HttpClientErrorException geworfen wird
    assertThrows(HttpClientErrorException.class, () -> userClientController.get(userId));

    // Hier keine Überprüfung von user, weil wir erwarten, dass eine Ausnahme geworfen wird.
    verify(restTemplate).getForObject(baseUrl + "/users/" + userId, User.class);
  }

  @Test
  void testUpdateUser() {
    String userId = "789";
    User mockUser = new User(); // Setze die Eigenschaften des Mock-Users hier

    userClientController.update(userId, mockUser);

    verify(restTemplate).put(baseUrl + "/users/" + userId, mockUser);
  }

  @Test
  void testDeleteUser() {
    String userId = "101";

    userClientController.delete(userId);

    verify(restTemplate).delete(baseUrl + "/users/" + userId);
  }

  @Test
  void testCreateUser() {
    User mockUser = new User(); // Setze die Eigenschaften des Mock-Users hier
    when(restTemplate.postForObject(eq(baseUrl + "/users"), eq(mockUser), eq(User.class))).thenReturn(mockUser);

    userClientController.create(mockUser);

    verify(restTemplate).postForObject(baseUrl + "/users", mockUser, User.class);
  }
}