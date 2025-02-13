package com.springboot.question.controller;

import com.springboot.auth.CustomPrincipal;
import com.springboot.auth.MemberDetailsService;
import com.springboot.member.service.MemberService;
import com.springboot.question.dto.QuestionDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/qna/questions")
@Validated
@Slf4j
public class QuestionController {
    private static String QUESTION_DEFAULT_URL = "/qna/questions";
    private final MemberService memberService;
    private final QuestionMapper questionMapper;
    private final QuestionService questionService;

    public QuestionController(MemberService memberService, QuestionMapper questionMapper, QuestionService questionService) {
        this.memberService = memberService;
        this.questionMapper = questionMapper;
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity postQuestion(@Valid @RequestBody QuestionDto.Post postDto,
                                       @AuthenticationPrincipal CustomPrincipal customPrincipal){
        Question question = questionMapper.questionPostToQuestion(postDto);
        postDto.setMemberId(customPrincipal.getMemberId());
        questionService.createQuestion(question);
    }
}
