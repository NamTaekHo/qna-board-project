package com.springboot.like.repository;

import com.springboot.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 중복 확인
    boolean existsByQuestionIdAndMemberId(long questionId, long memberId);
    // questionId 와 memberId 로 해당 Like 찾기
    Optional<Like> findByQuestionIdAndMemberId(long questionId, long memberId);
    // 질문에 대한 좋아요 수 계산
    int countByQuestionId(long questionId);
}
