package com.fnavas.BlogEngine.repository;

import com.fnavas.BlogEngine.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
