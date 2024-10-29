package ch.baloise.pactdemoconsumer.controller;

import ch.baloise.pactdemoconsumer.model.User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class UserClientController {

  private final String baseUrl;
  private final RestTemplate restTemplate;

  public UserClientController(String baseUrl, RestTemplate restTemplate) {
    this.baseUrl = baseUrl;
    this.restTemplate = restTemplate;
  }

  public UserClientController(String baseUrl) {
    this(baseUrl, new RestTemplate());
  }


  public User get(String userId) {
    try {
      return restTemplate.getForObject(baseUrl + "/users/" + userId, User.class);
    } catch (HttpClientErrorException.NotFound e) {
      return null;
    }
  }

  public void update(String userId, User user) {
    restTemplate.put(baseUrl + "/users/" + userId, user);
  }

  public void delete(String userId) {
    restTemplate.delete(baseUrl + "/users/" + userId);
  }

  public void create(User user) {
    restTemplate.postForObject(baseUrl + "/users", user, User.class);
  }
}

