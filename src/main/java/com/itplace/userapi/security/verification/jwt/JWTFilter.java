package com.itplace.userapi.security.verification.jwt;

import com.itplace.userapi.user.dto.UserDto;
import com.itplace.userapi.user.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component // Spring 컴포넌트로 등록
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청(Request)의 'Authorization' 헤더를 찾습니다.
        String authorizationHeader = request.getHeader("Authorization");

        // 2. 헤더가 없거나, 'Bearer '로 시작하지 않으면 다음 필터로 넘깁니다.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 'Bearer ' 부분을 제외한 순수 토큰만 추출합니다.
        String token = authorizationHeader.substring(7);

        // 4. 토큰이 만료되었는지 확인하고, 만료 시 다음 필터로 넘깁니다.
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String category = jwtUtil.getCategory(token);
        if (!"access".equals(category)) {
            // access 토큰이 아니면 요청을 거부
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // =================================================================================================
        // 1. 토큰에서 userId와 role 정보를 추출합니다.
        Long userId = jwtUtil.getUserId(token);
        Role role = jwtUtil.getRole(token);
        String provider = jwtUtil.getProvider(token);
        String providerId = jwtUtil.getProviderId(token);

        // 2. UserDto를 생성하여 현재 인증된 사용자의 정보를 담습니다. 이 객체가 'principal' 이 됩니다.
        //    (DB 조회 없이 토큰에 있는 정보만으로 인증 처리)
        UserDto userDto = UserDto.builder()
                .id(userId)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();

        // 3. role 정보를 바탕으로 Spring Security가 요구하는 authorities 목록을 생성합니다.
        var authorities = Collections.singletonList(new SimpleGrantedAuthority(role.name()));

        // 4. UserDto, authorities를 바탕으로 최종 인증(Authentication) 객체를 생성합니다.
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDto, null, authorities);

        // 5. 생성된 인증 객체를 SecurityContext에 등록하여, 현재 요청 동안 인증된 상태로 만듭니다.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}