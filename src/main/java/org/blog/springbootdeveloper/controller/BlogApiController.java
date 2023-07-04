package org.blog.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.domain.Article;
import org.blog.springbootdeveloper.dto.AddArticleRequest;
import org.blog.springbootdeveloper.dto.UpdateArticleRequest;
import org.blog.springbootdeveloper.service.BlogService;
import org.blog.springbootdeveloper.dto.ArticleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor  //final 이나 @NonNull 이 붙은 필드를 인자로 가지는 생성자를 자동으로 생성
@RestController
public class BlogApiController {
    private final BlogService blogService;

    //HTTP메소드가 POST일때 전달받은 URL과 동일하면 메소드를 매핑
    @PostMapping("/api/articles")  //글 생성
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @GetMapping("/api/articles")  //글 전체 조회
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}") //특정 id에 해당하는 글 조회
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity  .ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}") //특정 id에 해당하는 글 삭제
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {    // @PathVariable : 경로에서 변수를 추출할때 사용함, null이 될 가능성 없음(null이면 제대로 된 url이 아니기 때문에)
        blogService.delete(id); // 'BlogService' 객체의 delete메소드를 호출

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedArticle);
    }

}
