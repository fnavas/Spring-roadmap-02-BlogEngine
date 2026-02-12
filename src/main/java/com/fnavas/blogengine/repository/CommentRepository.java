package com.fnavas.blogengine.repository;

import com.fnavas.blogengine.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
