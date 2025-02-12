package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    public MemberService(MemberRepository memberRepository, QuestionRepository questionRepository) {
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
    }

    public Member createMember(Member member){
        // 중복인지 검증
        verifyExistsEmail(member.getEmail());
        // 저장
        return memberRepository.save(member);
    }

    public Member updateMember(Member member){
        // 존재하는지 검증
        Member findMember = findVerifiedMember(member.getMemberId());
        // 변경 가능한 필드 확인 후 변경
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));
        // 저장
        return memberRepository.save(findMember);
    }

    public Member findMember(long memberId){
        // 있는지 검증 후 반환
        return findVerifiedMember(memberId);
    }

    public Page<Member> findMembers(int page, int size){
        // page 번호 검증
        if(page < 1){
            throw new IllegalArgumentException("페이지의 번호는 1 이상이어야 합니다.");
        }
        // Page객체에 담아서 반환(내림차순)
        return memberRepository.findAll(PageRequest.of(page-1, size, Sort.by("memberId").descending()));
    }

    @Transactional
    public void deleteMember(long memberId){
        // 있는지 검증 후 가져와서
        Member findMember = findVerifiedMember(memberId);
        // 이미 탈퇴 상태면 예외 발생
        if(findMember.getMemberStatus() == Member.MemberStatus.MEMBER_QUIT){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        // 탈퇴 상태로 변경, deactivate 내부에서 question도 비활성화로 변경
        findMember.deactivate();
        // 저장
        memberRepository.save(findMember);
    }

    // 중복된 이메일이 있는지 검증
    private void verifyExistsEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    // 존재하는 멤버인지 검증
    public Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }
}
