package com.itplace.userapi.log.advice;

import com.itplace.userapi.benefit.dto.response.BenefitListResponse;
import com.itplace.userapi.benefit.dto.response.MapBenefitDetailResponse;
import com.itplace.userapi.benefit.dto.response.PagedResponse;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.log.service.LogService;
import com.itplace.userapi.security.auth.local.dto.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class BenefitLogAdvice implements ResponseBodyAdvice<Object> {

    private final LogService logService;

    private final BenefitRepository benefitRepsoitory;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        String path = req.getRequestURI();
        String param = req.getQueryString();
        Long userId = getUserId();

        String[] parts = path.split("/");
        if (!parts[parts.length - 1].equals("benefit") && !parts[parts.length - 2].equals("benefit")) {
            return body;
        }

        System.out.println("==== beforeBodyWrite 실행됨 ====");
        if (userId != null) {
            String partnerIdStr = req.getParameter("partnerId");
            Long partnerId = null;
            String event = null;
            if (partnerIdStr != null && !partnerIdStr.isBlank()) {
                partnerId = Long.valueOf(partnerIdStr);
                event = "detail";
            }

            if (!(body instanceof ApiResponse<?> topRes)) {
                return body;
            }

            Object data = topRes.getData();
            if (!(data instanceof PagedResponse<?> dataRes)) {
                System.out.println("==== saveResonseLog(detail) 저장 ====");
                if (data instanceof MapBenefitDetailResponse detail) {

                    System.out.println("data : " + data.toString());
                    long benefitId = detail.getBenefitId();
                    logService.saveResponseLog(
                            getUserId(),
                            event,
                            benefitId,
                            partnerId,
                            path,
                            param
                    );
                }
                return body;
            }

            Object content = dataRes.getContent();
            String keyword = req.getParameter("keyword");
            if (content instanceof List<?> list && keyword != null && !keyword.isBlank()) {
                for (Object item : list) {
                    if (item instanceof BenefitListResponse benefit) {
                        System.out.println("==== saveResonseLog(search) 저장 ====");
                        long benefitId = benefit.getBenefitId();
                        partnerId = benefit.getPartnerId();

                        logService.saveResponseLog(
                                getUserId(),
                                "search",
                                benefitId,
                                partnerId,
                                path,
                                param
                        );
                    }
                }
            }
        }

        return body;
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            return userDetails.getUser().getId();
        }
        log.info("user 정보 없음");
        return null;
    }

}
