package com.example.securityapi.controller;

import com.example.securityapi.dto.JwtResponse;
import com.example.securityapi.dto.LoginRequest;
import com.example.securityapi.model.User;
import com.example.securityapi.repository.UserRepository;
import com.example.securityapi.security.JwtUtils;
import com.example.securityapi.security.LoginAttemptService;
import com.example.securityapi.service.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для регистрации и входа пользователей")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;
  private final LoginAttemptService loginAttemptService;

  @Operation(
      summary = "Вход в систему",
      description = "Аутентификация пользователя с помощью логина и пароля")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Успешная аутентификация",
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные учетные данные",
            content = @Content(schema = @Schema(implementation = Map.class)))
      })
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    String username = loginRequest.getUsername();

    if (loginAttemptService.isBlocked(username)) {
      long remainingTime = loginAttemptService.getRemainingLockTime(username);
      Map<String, Object> error = new HashMap<>();
      error.put("message", "Аккаунт заблокирован за DDOS атаку");
      error.put("remainingLockTimeSeconds", remainingTime / 1000);
      return ResponseEntity.status(429).body(error);
    }

    try {

      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(username);

      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

      loginAttemptService.loginSucceeded(username);
      return ResponseEntity.ok(
          new JwtResponse(jwt, userPrincipal.getUsername(), userPrincipal.getEmail()));
    } catch (Exception e) {
      loginAttemptService.loginFailed(username);
      Map<String, String> error = new HashMap<>();
      error.put("message", "Invalid username or password");
      return ResponseEntity.badRequest().body(error);
    }
  }

  @Operation(
      summary = "Регистрация пользователя",
      description = "Создание нового аккаунта пользователя")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно зарегистрирован",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка регистрации (логин или email уже заняты)",
            content = @Content(schema = @Schema(implementation = Map.class)))
      })
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody User signUpRequest) {
    Map<String, String> response = new HashMap<>();

    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      response.put("message", "Error: Username is already taken!");
      return ResponseEntity.badRequest().body(response);
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      response.put("message", "Error: Email is already in use!");
      return ResponseEntity.badRequest().body(response);
    }

    User user =
        new User(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    userRepository.save(user);

    response.put("message", "User registered successfully!");
    return ResponseEntity.ok(response);
  }
}
