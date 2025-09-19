package com.example.securityapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос для входа в систему")
public class LoginRequest {

  @NotBlank(message = "Username is required")
  @Schema(description = "Имя пользователя", example = "admin")
  private String username;

  @NotBlank(message = "Password is required")
  @Schema(description = "Пароль", example = "password123")
  private String password;
}
