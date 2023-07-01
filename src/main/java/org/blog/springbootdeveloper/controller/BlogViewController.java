package org.blog.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.domain.Article;
import org.blog.springbootdeveloper.dto.ArticleListViewResponse;
import org.blog.springbootdeveloper.dto.ArticleViewResponse;
import org.blog.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogViewController {
    private final BlogService blogService; //위에 @RequiredArgsConstructor때문에 기본 생성자에 인자로 들어감.
//    public BlogViewController(BlogService blogService) {
//        this.blogService = blogService;
//    }  --> @RequiredArgsConstructor가 대체해줌

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id,Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    //id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id 없을 수도 있음)
    public String newArticle(@RequestParam(required = false) Long id,Model model) {
        if (id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }
}
