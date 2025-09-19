package com.example.securityapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponse {

  private String token;
  private String type = "Bearer";
  private String username;
  private String email;

  public JwtResponse(String token, String username, String email) {
    this.token = token;
    this.username = username;
    this.email = email;
  }
}
