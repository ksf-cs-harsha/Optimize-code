package com.harsha.organize.services;

import com.harsha.organize.entity.Comment;
import com.harsha.organize.entity.Post;
import com.harsha.organize.exception.ResourceNotFoundException;
import com.harsha.organize.repo.CommentRepository;
import com.harsha.organize.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    public final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Optional<Post> getPostById(int postId) {
        return postRepository.findById(postId);
    }

    public List<Post> getPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page -1, size)).getContent();
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        if (comments.isEmpty()) {
            throw new ResourceNotFoundException("No comments found for post with id " + postId);
        }
        return comments;
    }
}

