package com.springboot.like.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.entity.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import com.springboot.utils.AuthorizationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final QuestionService questionService;
    private final MemberService memberService;

    public LikeService(LikeRepository likeRepository, QuestionService questionService, MemberService memberService) {
        this.likeRepository = likeRepository;
        this.questionService = questionService;
        this.memberService = memberService;
    }

    @Transactional
    public void toggleLike(long questionId, long authenticatedId){
        Optional<Like> optionalLike = likeRepository.findByQuestion_QuestionIdAndMember_MemberId(questionId, authenticatedId);
        Question currentQuestion = questionService.findVerifiedQuestion(questionId);
        if(optionalLike.isPresent()){
            likeRepository.delete(optionalLike.get());
            currentQuestion.minusLikeCount();
        } else {
            Like like = new Like();
            like.setMember(memberService.findVerifiedMember(authenticatedId));
            like.setQuestion(currentQuestion);
            likeRepository.save(like);
            currentQuestion.addLikeCount();
        }
    }

//    @Transactional
//    public void addLike(long questionId, long memberId, long currentId) {
//        // Question 있는지 검증
//        Question question = questionService.findVerifiedQuestion(questionId);
//        // member 찾기
//        Member member = memberService.findMember(memberId, currentId);
//        // 이미 좋아요 눌렀는지 확인
//        if (likeRepository.existsByQuestion_QuestionIdAndMember_MemberId(questionId, memberId)) {
//            throw new BusinessLogicException(ExceptionCode.ALREADY_LIKED);
//        }
//        Like like = new Like();
//        like.setQuestion(question);
//        like.setMember(member);
//        likeRepository.save(like);
//        // Question 좋아요 수 증가
//        question.addLikeCount();
//    }
//
//    @Transactional
//    public void deleteLike(Long questionId, Long memberId) {
//        // 좋아요 기록 있는지 확인
//        Like like = likeRepository.findByQuestion_QuestionIdAndMember_MemberId(questionId, memberId).orElseThrow(
//                () -> new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND)
//        );
//        // 좋아요 DB에서 삭제
//        likeRepository.delete(like);
//        // Question 좋아요 수 감소
//        Question question = questionService.findVerifiedQuestion(questionId);
//        if (question.getLikeCount() < 1) {
//            throw new BusinessLogicException(ExceptionCode.CANNOT_UNLIKE);
//        }
//            question.minusLikeCount();
//    }
}
