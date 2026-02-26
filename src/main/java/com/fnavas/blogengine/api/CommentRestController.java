package com.fnavas.blogengine.api;

import com.fnavas.blogengine.service.CommentService;
import com.fnavas.blogengine.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

}
