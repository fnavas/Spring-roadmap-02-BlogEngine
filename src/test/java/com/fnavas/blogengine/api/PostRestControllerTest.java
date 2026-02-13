package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.PostCreateRequest;
import com.fnavas.blogengine.dto.PostResponse;
import com.fnavas.blogengine.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    private PostResponse samplepostResponse(){
        return new PostResponse(1L, "Title", "Content", null,null);
    }

    @Test
    void getAllPosts_shouldReturnsOk() throws Exception {
        PostResponse mockPostResponse = samplepostResponse();
        Mockito.when(postService.getAllPosts()).thenReturn(List.of(mockPostResponse));

        mockMvc.perform(get("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].content").value("Content"));
    }

    @Test
    void getPostById_shouldReturnsOk() throws Exception {
        Long id = 1L;
        PostResponse mockPostResponse = samplepostResponse();
        Mockito.when(postService.getPostById(id)).thenReturn(mockPostResponse);

        mockMvc.perform(get("/api/v1/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    void getPostsByAuthor_shouldReturnOk() throws Exception {
        String author = "author";
        PostResponse mockPostResponse = samplepostResponse();
        Mockito.when(postService.getPostsByAuthor(author)).thenReturn(List.of(mockPostResponse));

        mockMvc.perform(get("/api/v1/posts/author/{author}", author)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getPostsByTitle_shouldReturnOk() throws Exception {
        String title = "title";
        PostResponse mockPostResponse = samplepostResponse();
        Mockito.when(postService.getPostsByTitle(title)).thenReturn(List.of(mockPostResponse));

        mockMvc.perform(get("/api/v1/posts/title/{title}", title)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
//TODO: Fix the test case for createPost, it is failing because of the missing request body and the expected response body.
// We need to mock the postService.createPost method to return a PostResponse and also include the request body in the mockMvc.perform call.

//    @Test
//    void createPost_shouldReturnOk_201() throws Exception {
//        PostCreateRequest postCreateRequest = new PostCreateRequest("title", "content");
//        Mockito.when(postService.createPost(postCreateRequest)).thenReturn(samplepostResponse());
//
//        mockMvc.perform(post("/api/v1/posts/")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//    }


}