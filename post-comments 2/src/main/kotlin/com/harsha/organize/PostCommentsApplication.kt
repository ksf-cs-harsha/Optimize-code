package com.harsha.organize

import com.harsha.organize.config.WebClientConfig
import com.harsha.organize.service.DataLoadingService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@EnableAutoConfiguration
@Import(WebClientConfig::class)
class PostCommentsApplication(private val dataLoadingService: DataLoadingService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        runBlocking {
            dataLoadingService.loadPostsAndComments()
        }
    }


}

fun main(args: Array<String>) {
    runApplication<PostCommentsApplication>(*args)
}



