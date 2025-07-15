package com.itplace.userapi.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PageResultDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private boolean hasNext;

    public static <T> PageResultDto<T> of(Page<T> page) {
        return PageResultDto.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .hasNext(page.hasNext())
                .build();
    }
}

