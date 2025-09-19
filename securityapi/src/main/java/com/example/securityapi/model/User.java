package com.example.securityapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
  @Column(unique = true)
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Post> posts;

  public User() {
    this.createdAt = LocalDateTime.now();
  }

  public User(String username, String email, String password) {
    this();
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public List<Post> getPosts() {
    return posts == null ? Collections.emptyList() : Collections.unmodifiableList(posts);
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts == null ? new ArrayList<>() : new ArrayList<>(posts);
  }
}
