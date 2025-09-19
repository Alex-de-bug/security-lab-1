package com.example.securityapi.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostResponse {

  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private String authorUsername;
}
