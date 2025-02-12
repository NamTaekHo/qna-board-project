package com.springboot.member.service;

import com.springboot.auth.utils.AuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityUtils authorityUtils;

    public MemberService(MemberRepository memberRepository, QuestionRepository questionRepository, PasswordEncoder passwordEncoder, AuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    public Member createMember(Member member){
        // 중복인지 검증
        verifyExistsEmail(member.getEmail());
        // 비번 암호화
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);
        // 역할 초기화
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);
        // 저장
        return memberRepository.save(member);
    }

    public Member updateMember(Member member, long currentMemberId){
        // 존재하는지 검증
        Member findMember = findVerifiedMember(member.getMemberId());
        // 현재 로그인 된 사용자와 동일한지 확인
        verifyAccess(member.getMemberId(), currentMemberId);

        // 변경 가능한 필드 확인 후 변경
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
//        Optional.ofNullable(member.getMemberStatus())
//                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));
        // 저장
        return memberRepository.save(findMember);
    }

    public Member findMember(long memberId, long currentMemberId){
        // 로그인한 멤버와 동일한지 검증
        verifyAccess(memberId, currentMemberId);
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
    public void deleteMember(long memberId, long currentMemberId){
        // 로그인한 사용자와 동일한지, 관리자인지 확인
        verifyAccess(memberId, currentMemberId);
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

    // 관리자인지 확인하는 메서드
    private boolean isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    // 관리자인지 동일한지 확인하고 아니면 예외던지는 메서드
    public void verifyAccess(long requestedId, long currentId){
        if(requestedId != currentId && isAdmin()){
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_OPERATION);
        }
    }
}
