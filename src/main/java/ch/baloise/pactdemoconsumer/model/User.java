package ch.baloise.pactdemoconsumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @JsonProperty("id")
  private String id;
  @JsonProperty("name")
  private String name;
}
