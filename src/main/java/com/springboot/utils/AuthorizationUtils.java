package com.springboot.utils;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationUtils {

    // 관리자인지 확인하는 메서드
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    // 관리자인지 또는 동일한 사용자인지 확인하고 아니면 예외 던지는 메서드
    public static void isAdminOrOwner(long ownerId, long authenticatedId) {
        if (!isOwner(ownerId, authenticatedId) && !isAdmin()) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_OPERATION);
        }
    }

    // 로그인한 사용자와 작성자가 동일한지 확인하는 메서드
    public static boolean isOwner(long ownerId, long authenticatedId) {
        if (ownerId != authenticatedId) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_OPERATION);
        }
        return true;
    }
}
