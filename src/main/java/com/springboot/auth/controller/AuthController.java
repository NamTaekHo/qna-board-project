package com.springboot.auth.controller;

import com.springboot.auth.CustomPrincipal;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        CustomPrincipal customPrincipal =(CustomPrincipal) authentication.getPrincipal();
        Member member = memberService.findVerifiedMember(customPrincipal.getMemberId());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", member.getEmail());
        userInfo.put("name", member.getName());
        userInfo.put("memberId", member.getMemberId());

        return ResponseEntity.ok(userInfo);
    }
}

