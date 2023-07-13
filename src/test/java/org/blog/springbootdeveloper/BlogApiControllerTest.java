package org.blog.springbootdeveloper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blog.springbootdeveloper.domain.Article;
import org.blog.springbootdeveloper.domain.User;
import org.blog.springbootdeveloper.dto.AddArticleRequest;
import org.blog.springbootdeveloper.dto.UpdateArticleRequest;
import org.blog.springbootdeveloper.repository.BlogRepository;
import org.blog.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUP() {
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context)  //실제 애플리케이션의 설정을 그대로 가져와서 테스트에 사용함
                .build();
        blogRepository.deleteAll();  //테스트가 독립적으로 수행될 수 있게
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user,
                user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("addArticle: 블로그 글 추가 성공")
    @Test
    public void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        //객체를 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        //when
        //설정한 내용을 바탕으로 요청을 전송함
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);

        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        System.out.println(articles.get(0).getTitle());//확인용

        assertThat(articles.get(0).getContent()).isEqualTo(content);
        System.out.println(articles.get(0).getContent());//확인용

    }

    @DisplayName("findAllAtricles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        //given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

//        blogRepository.save(Article.builder()  //'id'필드는 자동 생성값이여서 앞에서  @Builder에 포함 안시켰음
//                .title(title)
//                .content(content)
//                .build());

        //when
        final ResultActions resultAction = mockMvc.perform(get(url)  //GET요청을 만들고 실행하는 부분, ResultActions 객체로 반환함
                .accept(MediaType.APPLICATION_JSON));                //이 요청에서 JSON형식의 응답을 기대한다라는 뜻

        //then
        resultAction
                .andExpect(status().isOk())   //HTTP 응답 상태 코드가 200 OK인지 확인, 요청이 성공적으로 처리됨
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()))       //응답 JSON의 첫 번째 항목의 'title'필드 값이 기대하는 'title'과 같은지 확인
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()));  //응답 JSON의 첫 번째 항목의 'content'필드 값이 기대하는 'content'과 같은지 확인

    }

    @DisplayName("findArticle: 블로그 글 조회에 성공")
    @Test
    public void findArticle() throws Exception {
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle=createDefaultArticle();
//        final String content = "content";
//        final String title = "title";

//        Article savedArticle = blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()))
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공")
    @Test
    public void deleteArticle() throws Exception {
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle=createDefaultArticle();
//        final String title = "title";
//        final String content = "content";

//        Article savedArticle = blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());

        //when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        //then
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공")
    @Test
    public void updateArticle() throws Exception {
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle=createDefaultArticle();
//        final String title = "title";
//        final String content = "content";

//        Article savedArticle = blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());

        //변경할 title, content
        final String newTitle = "new title";
        final String newConent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newConent);

        //when
        //perform 메서드를 통해 HTTP 요청을 MockMvc 인스턴스에게 전달,
        //perform 메서드는 ResultActions 객체를 반환,
        //이 객체를 사용하여 응답을 검증하거나 결과를 검증할 수 있음
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)        //클라이언트가 서버에게 보내는 데이터의 형태가 JSON이다는 것을 말함
                .content(objectMapper.writeValueAsString(request)));  //직렬화, request객체(수정할 게시글의 새로운 제목과 내용이 담긴 UpdateArticleRequest객체)를 JSON문자열로 변환

        //then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newConent);
    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }
}