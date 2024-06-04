package com.harsha.organize.controller;

import com.harsha.organize.entity.Comment;
import com.harsha.organize.entity.Post;
import com.harsha.organize.exception.ResourceNotFoundException;
import com.harsha.organize.output.CommentOutput;
import com.harsha.organize.output.PostOutput;
import com.harsha.organize.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v2/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentPostController {

    @Autowired
    private PostService postService;

    @GetMapping("post/{postId}")
    public PostOutput getPostWithComments(@PathVariable int postId) {
        Optional<Post> optionalPost = postService.getPostById(postId);
        if (optionalPost.isEmpty()) {
            throw new ResourceNotFoundException("Post not found for the given id " + postId);
        }

        Post post = optionalPost.get();
        List<Comment> comments = postService.getCommentsByPostId(postId);
        List<CommentOutput> commentOutputs = comments.stream()
                .map(comment -> new CommentOutput(comment.getId(), comment.getName(), comment.getEmail(), comment.getBody()))
                .collect(Collectors.toList());

        return new PostOutput(post.getId(), post.getUserId(), post.getName(), commentOutputs);
    }

    @GetMapping("/posts")
    public PostsResponseDTO getPosts(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = postService.getPosts(page, pageSize);

        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No posts found for page " + page);
        }

        List<PostOutput> postOutputs = posts.stream()
                .map(post -> {
                    List<CommentOutput> commentOutputs = postService.getCommentsByPostId(post.getId()).stream()
                            .map(comment -> new CommentOutput(comment.getId(), comment.getName(), comment.getEmail(), comment.getBody()))
                            .collect(Collectors.toList());

                    if (commentOutputs.isEmpty()) {
                        throw new ResourceNotFoundException("No comments found for post " + post.getId());
                    }

                    return new PostOutput(post.getId(), post.getUserId(), post.getName(), commentOutputs);
                })
                .collect(Collectors.toList());

        long total = postService.postRepository.count();
        return new PostsResponseDTO(postOutputs, page, total);
    }

    public record PostsResponseDTO(List<PostOutput> feeds, int page, long total) {
    }
}
