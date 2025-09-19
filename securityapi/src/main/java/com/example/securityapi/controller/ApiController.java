package com.example.securityapi.controller;

import com.example.securityapi.dto.PostRequest;
import com.example.securityapi.dto.PostResponse;
import com.example.securityapi.model.Post;
import com.example.securityapi.model.User;
import com.example.securityapi.repository.UserRepository;
import com.example.securityapi.service.PostService;
import com.example.securityapi.service.UserPrincipal;
import com.example.securityapi.util.Sanitizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@Tag(name = "API", description = "Защищенные endpoints для работы с постами и профилем")
@SecurityRequirement(name = "Bearer Authentication")
public class ApiController {

  private static final String UNKNOWN_AUTHOR = "Unknown";
  private static final String TOTAL_POSTS_KEY = "totalPosts";
  private static final String POSTS_KEY = "posts";
  private static final String MESSAGE_KEY = "message";
  private static final String USER_NOT_FOUND_MESSAGE = "User not found";

  private final PostService postService;
  private final UserRepository userRepository;

  public ApiController(PostService postService, UserRepository userRepository) {
    this.postService = Objects.requireNonNull(postService, "PostService cannot be null");
    this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
  }

  @Operation(summary = "Получить все посты", description = "Возвращает список всех постов")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список постов успешно получен",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Неавторизованный доступ",
            content = @Content())
      })
  @GetMapping("/data")
  public ResponseEntity<Map<String, Object>> getAllData() {
    try {
      List<Post> posts = postService.getAllPosts();
      List<PostResponse> postResponses =
          posts.stream()
              .map(
                  post -> {
                    Post.UserInfo authorInfo = post.getAuthorInfo();
                    return new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt(),
                        authorInfo != null ? authorInfo.getUsername() : UNKNOWN_AUTHOR);
                  })
              .toList();

      Map<String, Object> response = new HashMap<>();
      response.put(POSTS_KEY, postResponses);
      response.put(TOTAL_POSTS_KEY, postResponses.size());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put(MESSAGE_KEY, "Error: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  @Operation(
      summary = "Создать пост",
      description = "Создает новый пост от имени текущего пользователя")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пост успешно создан",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации данных",
            content = @Content()),
        @ApiResponse(
            responseCode = "401",
            description = "Неавторизованный доступ",
            content = @Content())
      })
  @PostMapping("/posts")
  public ResponseEntity<Map<String, Object>> createPost(
      @Valid @RequestBody PostRequest postRequest) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

      User user =
          userRepository
              .findByUsername(userPrincipal.getUsername())
              .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

      String sanitizedTitle = Sanitizer.forHtml(postRequest.getTitle());
      String sanitizedContent = Sanitizer.forHtml(postRequest.getContent());

      Post post = new Post(sanitizedTitle, sanitizedContent, user);
      Post savedPost = postService.createPost(post);

      Post.UserInfo authorInfo = savedPost.getAuthorInfo();
      PostResponse postResponse =
          new PostResponse(
              savedPost.getId(),
              savedPost.getTitle(),
              savedPost.getContent(),
              savedPost.getCreatedAt(),
              authorInfo != null ? authorInfo.getUsername() : UNKNOWN_AUTHOR);

      Map<String, Object> response = new HashMap<>();
      response.put(MESSAGE_KEY, "Post created successfully");
      response.put("post", postResponse);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put(MESSAGE_KEY, "Error creating post: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  @Operation(
      summary = "Получить мои посты",
      description = "Возвращает все посты текущего пользователя")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Посты пользователя успешно получены",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Неавторизованный доступ",
            content = @Content())
      })
  @GetMapping("/posts/my")
  public ResponseEntity<Map<String, Object>> getMyPosts() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

      User user =
          userRepository
              .findByUsername(userPrincipal.getUsername())
              .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

      List<Post> posts = postService.getPostsByUser(user);
      List<PostResponse> postResponses =
          posts.stream()
              .map(
                  post -> {
                    Post.UserInfo authorInfo = post.getAuthorInfo();
                    return new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt(),
                        authorInfo != null ? authorInfo.getUsername() : UNKNOWN_AUTHOR);
                  })
              .toList();

      Map<String, Object> response = new HashMap<>();
      response.put(POSTS_KEY, postResponses);
      response.put(TOTAL_POSTS_KEY, postResponses.size());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put(MESSAGE_KEY, "Error retrieving user posts: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  @Operation(summary = "Получить профиль", description = "Возвращает профиль текущего пользователя")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Профиль пользователя успешно получен",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Неавторизованный доступ",
            content = @Content())
      })
  @GetMapping("/profile")
  public ResponseEntity<Map<String, Object>> getUserProfile() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

      User user =
          userRepository
              .findByUsername(userPrincipal.getUsername())
              .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

      Map<String, Object> response = new HashMap<>();
      response.put("id", user.getId());
      response.put("username", user.getUsername());
      response.put("email", user.getEmail());
      response.put(TOTAL_POSTS_KEY, user.getPosts().size());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put(MESSAGE_KEY, "Error retrieving profile: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }
}
