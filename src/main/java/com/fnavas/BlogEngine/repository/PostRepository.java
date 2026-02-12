package com.fnavas.BlogEngine.repository;

import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);

    List<Post> findByTitleContainingIgnoreCase(String title);
}
