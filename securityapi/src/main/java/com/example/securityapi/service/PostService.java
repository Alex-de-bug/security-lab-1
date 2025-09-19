package com.example.securityapi.service;

import com.example.securityapi.model.Post;
import com.example.securityapi.model.User;
import com.example.securityapi.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  @Autowired private PostRepository postRepository;

  public List<Post> getAllPosts() {
    return postRepository.findByOrderByCreatedAtDesc();
  }

  public List<Post> getPostsByUser(User user) {
    return postRepository.findByAuthorOrderByCreatedAtDesc(user);
  }

  public Optional<Post> getPostById(Long id) {
    return postRepository.findById(id);
  }

  public Post createPost(Post post) {
    return postRepository.save(post);
  }

  public void deletePost(Long id) {
    postRepository.deleteById(id);
  }
}
