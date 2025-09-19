package com.example.securityapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Setter
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

  @NotBlank(message = "Title is required")
  @Size(max = 200, message = "Title must not exceed 200 characters")
  @Getter
  private String title;

  @NotBlank(message = "Content is required")
  @Size(max = 1000, message = "Content must not exceed 1000 characters")
  @Column(columnDefinition = "TEXT")
  @Getter
  private String content;

  @Column(name = "created_at")
  @Getter
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  @JsonBackReference
  private User author;

  public Post() {
    this.createdAt = LocalDateTime.now();
  }

  public Post(String title, String content, User author) {
    this();
    this.title = Objects.requireNonNull(title, "Title cannot be null");
    this.content = Objects.requireNonNull(content, "Content cannot be null");
    this.author = Objects.requireNonNull(author, "Author cannot be null");
  }

  public UserInfo getAuthorInfo() {
    if (this.author == null) {
      return null;
    }
    return new UserInfo(this.author.getId(), this.author.getUsername(), this.author.getEmail());
  }

  public void setAuthor(@NotNull User author) {
    this.author = Objects.requireNonNull(author, "Author cannot be null");
  }

  public static class UserInfo {
    private final Long id;
    private final String username;
    private final String email;

    public UserInfo(Long id, String username, String email) {
      this.id = id;
      this.username = username;
      this.email = email;
    }

    public Long getId() {
      return id;
    }

    public String getUsername() {
      return username;
    }

    public String getEmail() {
      return email;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      UserInfo userInfo = (UserInfo) o;
      return Objects.equals(id, userInfo.id)
          && Objects.equals(username, userInfo.username)
          && Objects.equals(email, userInfo.email);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, username, email);
    }
  }
}
