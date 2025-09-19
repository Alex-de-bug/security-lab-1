package com.example.securityapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос для создания нового поста")
public class PostRequest {

  @NotBlank(message = "Title is required")
  @Size(max = 200, message = "Title must not exceed 200 characters")
  @Schema(description = "Заголовок поста", example = "Мой первый пост")
  private String title;

  @NotBlank(message = "Content is required")
  @Size(max = 1000, message = "Content must not exceed 1000 characters")
  @Schema(description = "Содержимое поста", example = "Это содержимое моего первого поста")
  private String content;
}
