package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import com.springboot.validator.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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

        private Question.Visibility visibility;

        private long memberId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    public static class Patch{
        private long questionId;

        @NotSpace
        private String title;

        @NotSpace
        private String content;

        private long memberId;

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
        private String memberName;
        private AnswerDto.Response answer;
        private String questionImage;
        private int likeCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public String getQuestionStatus(){
            return questionStatus.getStatus();
        }
        public String getVisibility(){
            return visibility.getStatus();
        }
    }
}
