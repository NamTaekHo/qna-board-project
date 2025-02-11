package com.springboot.member;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member){
        // 중복인지 검증
        verifyExistsEmail(member.getEmail());
        return memberRepository.save(member);
    }

    public Member updateMember(Member member){
        Member findMember = findVerifiedMember(member.getMemberId());

        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));
        return memberRepository.save(findMember);
    }

    public Member findMember(long memberId){
        return findVerifiedMember(memberId);
    }

    public Page<Member> findMembers(int page, int size){
        return memberRepository.findAll(PageRequest.of(page-1, size, Sort.by("memberId").descending()));
    }

    public void deleteMember(long memberId){
        Member findMember = findVerifiedMember(memberId);
        if(findMember.getMemberStatus() == Member.MemberStatus.MEMBER_QUIT){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
        memberRepository.save(findMember);
    }

    private void verifyExistsEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    private Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }
}
