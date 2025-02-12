package com.springboot.member.controller;

import com.springboot.auth.MemberDetailsService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RequestMapping("/qna/members")
@Validated
@RestController
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/qna/members";
    private final MemberService memberService;
    private final MemberMapper mapper;

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post postDto){
        Member member = mapper.memberPostToMember(postDto);
        Member createdMember = memberService.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createdMember.getMemberId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(
            @PathVariable("member-id") @Positive long memberId,
            @Valid @RequestBody MemberDto.Patch patchDto,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ){
        patchDto.setMemberId(memberId);
        Member member = memberService.updateMember(mapper.memberPatchToMember(patchDto), memberDetails.getMemberId());
        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);
    }

    @GetMapping("/{member-id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or #memberId == principal.memberId")
    public ResponseEntity getMember(
            @PathVariable("member-id") @Positive long memberId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails){
        Member member = memberService.findMember(memberId, memberDetails.getMemberId());
        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getMembers(@Positive @RequestParam int page, @Positive @RequestParam int size){
        Page<Member> memberPage = memberService.findMembers(page, size);
        List<Member> members = memberPage.getContent();
        return new ResponseEntity(new MultiResponseDto<>(
                mapper.membersToMemberResponses(members), memberPage
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or #memberId == principal.memberId")
    public ResponseEntity deleteMember(@PathVariable("member-id") long memberId,
                                       @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails){
        memberService.deleteMember(memberId, memberDetails.getMemberId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
