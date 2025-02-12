package com.springboot.question.service;

import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public QuestionService(QuestionRepository questionRepository, MemberService memberService) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }

    public Question createQuestion(Question question){
        memberService.findVerifiedMember(question.getMember().getMemberId());
        return questionRepository.save(question);
    }

//    private void verifyQuestion(Question question){
//        memberService.findVerifiedMember(question.getMember().getMemberId());
//
//    }
}
