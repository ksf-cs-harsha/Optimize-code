package com.harsha.organize.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private String name;
    private String body;
    private LocalDateTime createAt;
    private LocalDateTime updateAt= LocalDateTime.now();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}


