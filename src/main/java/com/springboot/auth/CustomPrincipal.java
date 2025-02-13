package com.springboot.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomPrincipal {
    private final String email;
    private final Long memberId;

}
