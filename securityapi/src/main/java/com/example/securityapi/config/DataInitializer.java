package com.example.securityapi.config;

import com.example.securityapi.model.Post;
import com.example.securityapi.model.User;
import com.example.securityapi.repository.PostRepository;
import com.example.securityapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.count() == 0) {
      User user1 = new User("admin", "admin@example.com", passwordEncoder.encode("password123"));
      User user2 = new User("Denichenko", "titkos02@mail.ru", passwordEncoder.encode("qwerty123"));
      User user3 =
          new User("Student367193", "367193@se.itmo.ru", passwordEncoder.encode("itmo2025"));

      userRepository.save(user1);
      userRepository.save(user2);
      userRepository.save(user3);

      Post post1 = new Post("Знакомство", "Привет, я администратор этого сайта", user1);

      Post post2 =
          new Post(
              "О себе",
              "Я обучаюсь с университете ИТМО и сейчас делаю лабоработрную по информационной"
                  + " безопасности",
              user2);

      Post post3 = new Post("Пост студента", "Мой ИСУ 367193", user3);

      postRepository.save(post1);
      postRepository.save(post2);
      postRepository.save(post3);
    }
  }
}
