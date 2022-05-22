package com.example.chmura1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@SpringBootApplication
class Chmura1Application {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @PostConstruct
    fun startup() {
        println("""
            Data uruchomienia: ${LocalDateTime.now()}
            Autor: Arkadiusz Krawczyk
            Nasłuchuje połączeń na porcie 8080
            """.trimIndent())
    }
}

fun main(args: Array<String>) {
    runApplication<Chmura1Application>(*args)
}


