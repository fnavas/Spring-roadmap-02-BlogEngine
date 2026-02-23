package com.fnavas.blogengine.repository;

import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    List<Post> findByAuthor(User author);

    List<Post> findByTitleContainingIgnoreCase(String title);
}
