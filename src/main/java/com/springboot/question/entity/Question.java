package com.springboot.question.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Visibility visibility = Visibility.QUESTION_PUBLIC;

    @Column(nullable = false)
    private int viewCount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public enum QuestionStatus{
        QUESTION_REGISTERED("질문 등록"),
        QUESTION_ANSWERED("답변 완료"),
        QUESTION_DELETED("삭제된 질문"),
        QUESTION_DEACTIVED("비활성화된 질문");

        @Getter
        private String status;

        QuestionStatus(String status){
            this.status = status;
        }
    }

    public enum Visibility {
        QUESTION_PUBLIC("공개글"),
        QUESTION_SECRET("비밀글");

        @Getter
        private String status;

        Visibility(String status){
            this.status = status;
        }
    }
}
