package com.springboot.question.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.auth.CustomPrincipal;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.like.service.LikeService;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.dto.QuestionDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.service.QuestionService;
import com.springboot.utils.AuthorizationUtils;
import com.springboot.utils.UriCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/qna/questions")
@Validated
@Slf4j
public class QuestionController {
    private static final String QUESTION_DEFAULT_URL = "/qna/questions";
    private final QuestionMapper questionMapper;
    private final QuestionService questionService;
    private final LikeService likeService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    public QuestionController(QuestionMapper questionMapper, QuestionService questionService, LikeService likeService, MemberService memberService, ObjectMapper objectMapper) {
        this.questionMapper = questionMapper;
        this.questionService = questionService;
        this.likeService = likeService;
        this.memberService = memberService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity postQuestion(@Valid @RequestPart("postDto") String postDtoJson,
                                       @RequestPart(value = "questionImage", required = false) MultipartFile questionImage,
                                       @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        try {
            QuestionDto.Post postDto = objectMapper.readValue(postDtoJson, QuestionDto.Post.class);
            // dto에 memberId set
            postDto.setMemberId(customPrincipal.getMemberId());
            // mapper로 dto -> entity
            Question question = questionMapper.questionPostToQuestion(postDto);
            // question만들고
            Question createdQuestion = questionService.createQuestion(question, questionImage);
            // URI 만들기
            URI location = UriCreator.createUri(QUESTION_DEFAULT_URL, createdQuestion.getQuestionId());
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        }
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(
            @PathVariable("question-id") @Positive long questionId,
            @Valid @RequestBody QuestionDto.Patch patchDto,
            @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        patchDto.setQuestionId(questionId);
        patchDto.setMemberId(customPrincipal.getMemberId());
        Question updatedQuestion = questionService.updateQuestion(questionMapper
                .questionPatchToQuestion(patchDto), customPrincipal.getMemberId());
        return new ResponseEntity(
                new SingleResponseDto<>(questionMapper.questionToQuestionResponse(updatedQuestion)), HttpStatus.OK);
    }

    @GetMapping("/{question-id}")
    public ResponseEntity getQuestion(
            @PathVariable("question-id") @Positive long questionId,
            @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        Question question = questionService.findQuestion(
                questionId, customPrincipal.getMemberId(), AuthorizationUtils.isAdmin());
        return new ResponseEntity(new SingleResponseDto<>(questionMapper.questionToQuestionResponse(question)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getQuestions(@Positive @RequestParam int page, @Positive @RequestParam int size,
                                       @RequestParam(defaultValue = "newest") String sortType,
                                       @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        Member currentMember = memberService.findVerifiedMember(customPrincipal.getMemberId());
        Page<Question> questionPage = questionService.findQuestions(page, size, sortType, currentMember);
        List<Question> questions = questionPage.getContent();
        return new ResponseEntity(new MultiResponseDto<>
                (questionMapper.questionsToQuestionResponses(questions), questionPage), HttpStatus.OK);
    }

    @DeleteMapping("/{question-id}")
    public ResponseEntity deleteQuestion(@PathVariable("question-id") long questionId,
                                         @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        questionService.deleteQuestion(questionId, customPrincipal.getMemberId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    // Like 요청 주소
    @PostMapping("/{question-id}/like")
    public ResponseEntity toggleLike(
            @PathVariable("question-id") long questionId,
            @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        likeService.toggleLike(questionId, customPrincipal.getMemberId());

        return new ResponseEntity(HttpStatus.OK);
    }

}
