package com.fnavas.BlogEngine.repositiry;

import com.fnavas.BlogEngine.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
