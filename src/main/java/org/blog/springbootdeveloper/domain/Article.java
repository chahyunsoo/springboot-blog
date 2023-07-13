package org.blog.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자 대신 생성해줌
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //1씩 증가, 새로운 엔티티를 저장할때마다 primary key를 자동 생성
    @Column(name="id",updatable = false)
    private Long id;

    @Column(name="title",nullable = false)
    private String title;

    @Column(name="content",nullable = false)
    private String content;

    @Column(name="author", nullable = false)
    private String author;

    @Builder
    public Article(String author,String title, String content){    //'id' 필드는 DB에서 자동으로 생성되는 값이여서(위의 주석 참고)
        this.author = author;
        this.title = title;
        this.content = content;
    }

    //update 메소드를 Article(도메인 모델)에 위치시킨 이유
    //Article 클래스 내에 update 메서드를 두는 것은 객체 지향 프로그래밍의 캡슐화 원칙을 따르는 것이며
    //이로 인해 코드의 유지 관리와 데이터의 안정성이 향상됩니다.
    //
    public void update(String title,String content) {
        this.title = title;
        this.content = content;
    }

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
