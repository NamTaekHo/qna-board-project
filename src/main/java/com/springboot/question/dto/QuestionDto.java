package com.springboot.question.dto;

import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


public class QuestionDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    public static class Post{
        @NotBlank(message = "제목은 필수 입력란입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력란입니다.")
        private String content;

        @NotBlank
        private Question.Visibility visibility;

        @NotBlank
        private long memberId;
    }

    @Getter
    @AllArgsConstructor
    @Setter
    public static class Patch{
        private long questionId;

        @NotBlank
        private String title;

        @NotBlank
        private String content;

        @NotBlank
        private Question.Visibility visibility;
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private long questionId;
        private String title;
        private String content;
        private Question.QuestionStatus questionStatus;
        private Question.Visibility visibility;
        private int viewCount;
        private long memberId;
        private long answerId;
        private int likeCount;
        private LocalDateTime createdAt;

        public String getQuestionStatus(){
            return questionStatus.getStatus();
        }
        public String getVisibility(){
            return visibility.getStatus();
        }
    }
}
