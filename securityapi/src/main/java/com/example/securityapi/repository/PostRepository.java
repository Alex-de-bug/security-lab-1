package com.example.securityapi.repository;

import com.example.securityapi.model.Post;
import com.example.securityapi.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByAuthor(User author);

  List<Post> findByOrderByCreatedAtDesc();

  List<Post> findByAuthorOrderByCreatedAtDesc(User author);
}
