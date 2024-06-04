package com.harsha.organize;


import com.harsha.organize.controller.DataLoadingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PostCommentsApplication implements CommandLineRunner {

    private final DataLoadingService dataLoadingService;

    public PostCommentsApplication(DataLoadingService dataLoadingService) {
        this.dataLoadingService = dataLoadingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PostCommentsApplication.class, args);

    }


    @Override
    public void run(String... args) throws Exception {
        dataLoadingService.loadPostsAndComments();
    }
}

