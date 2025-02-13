package com.springboot.answer.controller;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.Answer;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.answer.service.AnswerService;
import com.springboot.auth.CustomPrincipal;
import com.springboot.dto.SingleResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/qna/questions/{question-id}/answers")
@Validated
public class AnswerController {
    private final AnswerService answerService;
    private final AnswerMapper mapper;

    public AnswerController(AnswerService answerService, AnswerMapper mapper) {
        this.answerService = answerService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postAnswer(@PathVariable("question-id") long questionId,
                                     @Valid @RequestBody AnswerDto.Post postDto,
                                     @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        postDto.setQuestionId(questionId);
        postDto.setMemberId(customPrincipal.getMemberId());
        Answer answer = mapper.answerPostToAnswer(postDto);
        Answer createdAnswer = answerService.createAnswer(answer);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/{answer-id}")
    public ResponseEntity patchAnswer(@PathVariable("answer-id") @Positive long answerId,
                                      @Valid @RequestBody AnswerDto.Patch patchDto,
                                      @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        patchDto.setAnswerId(answerId);
        Answer updatedAnswer = answerService.updateAnswer(mapper.answerPatchToAnswer(patchDto));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{answer-id}")
    public ResponseEntity deleteAnswer(@PathVariable("answer-id") long answerId) {
        answerService.deleteAnswer(answerId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
