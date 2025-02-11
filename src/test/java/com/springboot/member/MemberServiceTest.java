package com.springboot.member;

import com.springboot.exception.BusinessLogicException;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void createMemberTest(){
        // given
        Member member = new Member();
        member.setEmail("test@gmail.com");
        member.setPassword("1111");
        member.setName("테스트");
        member.setPhone("010-1111-1111");

        given(memberRepository.findByEmail(Mockito.anyString())).willReturn(Optional.of(member));

        // when, then
        assertThrows(BusinessLogicException.class, () -> memberService.createMember(member));
    }

    @Test
    public void updateMemberTest(){
        // given

    }
}