package com.springboot.question.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.audit.BaseEntity;
import com.springboot.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @NotBlank(message = "제목을 입력해주세요.")
    @Column(length = 50, nullable = false)
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Visibility visibility = Visibility.QUESTION_PUBLIC;

    @Column(nullable = false)
    private int viewCount = 0;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Answer answer;

    @Column(nullable = false)
    private int likeCount = 0;

    public void setMember(Member member){
        this.member = member;
        if(!member.getQuestions().contains(this)){
            member.setQuestion(this);
        }
    }

    public void deactivate(){
        this.questionStatus = QuestionStatus.QUESTION_DEACTIVED;
    }

    public void setAnswer(Answer answer){
        this.answer = answer;
        if(answer != null){
            answer.setQuestion(this);
            this.questionStatus = QuestionStatus.QUESTION_ANSWERED;
        }
    }

    public void addLikeCount(){
        this.likeCount += 1;
    }

    public void minusLikeCount(){
        this.likeCount -= 1;
    }

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
