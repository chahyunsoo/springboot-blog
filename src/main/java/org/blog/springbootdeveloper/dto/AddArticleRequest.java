package org.blog.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.blog.springbootdeveloper.domain.Article;

@NoArgsConstructor //기본 생성자를 추가함
@AllArgsConstructor //모든 필드 값을 파라미터로 받게하는 생성자를 추가함
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
