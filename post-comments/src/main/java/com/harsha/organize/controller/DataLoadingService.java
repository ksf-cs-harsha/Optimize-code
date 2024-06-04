package com.harsha.organize.controller;

import com.harsha.organize.dto.CommentData;
import com.harsha.organize.dto.PostData;
import com.harsha.organize.entity.Comment;
import com.harsha.organize.entity.Post;
import com.harsha.organize.repo.CommentRepository;
import com.harsha.organize.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataLoadingService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Autowired
    public DataLoadingService(PostRepository postRepository, CommentRepository commentRepository, RestTemplate restTemplate) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.restTemplate = restTemplate;
    }

    public void loadPostsAndComments() {
        log.info("In loadPostsAndComments");

        CompletableFuture.supplyAsync(() -> {
            List<PostData> postDataList = restTemplate.exchange(
                    BASE_URL + "/posts",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PostData>>() {}
            ).getBody();

            if (postDataList != null) {
                log.info("Downloaded posts = {}", postDataList.size());
                return postDataList.stream().map(this::savePostWithComments).collect(Collectors.toList());
            } else {
                return List.of();
            }
        }, executorService).join();

        log.info("Return from loadPostsAndComments");
    }

    private CompletableFuture<Void> savePostWithComments(PostData postData) {
        return CompletableFuture.runAsync(() -> {
            Post post = new Post();
            post.setUserId(postData.userId());
            post.setId(postData.id());
            post.setName(postData.title());
            post.setBody(postData.body());
            post.setCreateAt(LocalDateTime.now());

            post = postRepository.save(post);

            List<CommentData> commentDataList = restTemplate.exchange(
                    BASE_URL + "/comments?postId=" + postData.id(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CommentData>>() {}
            ).getBody();

            if (commentDataList != null) {
                log.info("Downloaded comments for post {} = {}", postData.id(), commentDataList.size());
                Post finalPost = post;
                List<Comment> comments = commentDataList.stream().map(commentData -> {
                    Comment comment = new Comment();
                    comment.setId(commentData.id());
                    comment.setName(commentData.name());
                    comment.setEmail(commentData.email());
                    comment.setBody(commentData.body());
                    comment.setPost(finalPost);
                    comment.setCreateAt(LocalDateTime.now());
                    return comment;
                }).collect(Collectors.toList());

                commentRepository.saveAll(comments);
            }
        }, executorService);
    }
}
