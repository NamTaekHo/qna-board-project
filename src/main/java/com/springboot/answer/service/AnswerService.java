package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import com.springboot.utils.AuthorizationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberService memberService;
    private final QuestionService questionService;

    public AnswerService(AnswerRepository answerRepository, MemberService memberService, QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.memberService = memberService;
        this.questionService = questionService;
    }

    public Answer createAnswer(Answer answer) {
        // 멤버 있는지 검증
        memberService.findVerifiedMember(answer.getMember().getMemberId());
        // 관리자인지 검증
        AuthorizationUtils.isAdmin();
        // 질문이 있는지, 답변이 이미 있는지 검증
        Question question = verifyExistsAnswerInQuestion(answer);
        // 검증 다 통과하면 해당 질문의 visibility 상태 따라서 set
        if (question.getVisibility() == Question.Visibility.QUESTION_SECRET) {
            answer.setAnswerStatus(Answer.AnswerStatus.ANSWER_SECRET);
        }
        // 저장
        return answerRepository.save(answer);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Answer updateAnswer(Answer answer){
        // 관리자인지 확인
        AuthorizationUtils.isAdmin();
        // 답변 있는지 검증
        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());
        // 내용이 바뀌었으면 바꾼 후 저장
        Optional.ofNullable(answer.getContent())
                .ifPresent(content -> findAnswer.setContent(content));
        return answerRepository.save(findAnswer);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAnswer(long answerId){
        // 관리자인지 확인
        AuthorizationUtils.isAdmin();
        // 답변 있는지 확인
        Answer answer = findVerifiedAnswer(answerId);
        questionService.setAnswerNull(answer.getQuestion().getQuestionId());
        answerRepository.deleteById(answerId);
    }

    // 질문에 답변이 있는지 검증 후 질문 객체 반환
    private Question verifyExistsAnswerInQuestion(Answer answer) {
        // answer에 담긴 questionId로 question 있는지 검증 후 있으면 객체에 답변 있는지 검증
        Question question = questionService.findVerifiedQuestion(answer.getQuestion().getQuestionId());
        if (question.getAnswer() != null) {
            throw new BusinessLogicException(ExceptionCode.ANSWER_EXISTS);
        }
        return question;
    }

    // 답변이 존재하는지 검증
    private Answer findVerifiedAnswer(long answerId){
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        return optionalAnswer.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND)
        );
    }
}
