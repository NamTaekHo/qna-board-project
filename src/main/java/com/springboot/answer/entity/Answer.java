package com.springboot.answer.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
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
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

//    @NotBlank(message = "답변 제목을 입력해주세요.")
//    @Column(nullable = false, length = 50)
//    private String title;

    @NotBlank(message = "답변 내용을 입력해주세요.")
    @Column(nullable = false, length = 255)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AnswerStatus answerStatus = AnswerStatus.ANSWER_PUBLIC;

    @OneToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void setQuestion(Question question){
        this.question = question;
        if(question != null && question.getAnswer() != this){
            question.setAnswer(this);
        }
    }

    public String getTitle(){
        return "[RE] " + this.question.getTitle();
    }

    public enum AnswerStatus{
        ANSWER_PUBLIC("공개 답변"),
        ANSWER_SECRET("비공개 답변");

        @Getter
        private String status;

        AnswerStatus(String status){
            this.status = status;
        }
    }
}
