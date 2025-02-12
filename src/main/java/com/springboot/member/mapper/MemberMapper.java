package com.springboot.member.mapper;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post postDto);
    MemberDto.Response memberToMemberResponse(Member member);
    Member memberPatchToMember(MemberDto.Patch patchDto);
    List<MemberDto.Response> membersToMemberResponses(List<Member> members);
}
