package org.blog.springbootdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  //created_at,updated_at 자동 업데이트 시킴
@SpringBootApplication
public class SpringBootDeveloperApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(SpringBootDeveloperApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
