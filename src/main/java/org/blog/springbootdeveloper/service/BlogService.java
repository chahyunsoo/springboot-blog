package org.blog.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.domain.Article;
import org.blog.springbootdeveloper.dto.AddArticleRequest;
import org.blog.springbootdeveloper.dto.UpdateArticleRequest;
import org.blog.springbootdeveloper.repository.BlogRepository;
import org.hibernate.sql.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }

}

//
//@RequiredArgsConstructor //final이 붙거나 @NotNull이 붙은 필드의 생성자 추가, 불변성 보장
//@Service //서비스 계층이다, 빈으로 등록
//public class BlogService {
//
//    private final BlogRepository blogRepository; //BlogService가 필요한 데이터를 저장하거나 불러올때 'BlogRepository'의 save메소드를 호출하니까
//
//    //블로그 글 추가 메소드
//    //AddArticleRequest DTO를 toEntity()를 통해 'Article'엔티티로 변환후,
//    //BlogRepository의 save메소드를 통해 DB에 저장함
//    public Article save(AddArticleRequest request,String userName){
//        return blogRepository.save(request.toEntity(userName)); //DTO를 받아 엔티티 객체로 변환한 후, DB에 저장
//    }
//
//    public List<Article> findAll(){
//        return blogRepository.findAll();
//    }
//
//    public Article findById(long id) {  //'id'를 받고 엔티티 조회하고 'Article'객체 없으면 IllegalArgumentException 예외를 발생
//        return blogRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
//    }
//    public void delete(long id){
////        blogRepository.deleteById(id);
//        Article article = blogRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//
//        authorizeArticleAuthor(article);
//        blogRepository.delete(article);
//    }
//
//    @Transactional
//    public Article update(long id, UpdateArticleRequest request) {
//        Article article = blogRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//
//        authorizeArticleAuthor(article);
//        article.update(request.getTitle(), request.getContent());
//
//        return article;
//    }
//
//    //게시글을 작성한 User인지 확인
//    private static void authorizeArticleAuthor(Article article) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        if (!article.getAuthor().equals(userName)) {
//            throw new IllegalArgumentException("not authorized");
//        }
//    }
//
//
//}
