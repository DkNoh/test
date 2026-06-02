package com.example.sms.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponseDTO<T> {

    private List<T> contents;
    private int     page;
    private int     size;
    private long    totalCount;
    private int     totalPages;
    private boolean hasNext;
    private boolean hasPrev;

    public static <T> PageResponseDTO<T> of(List<T> contents, PageRequestDTO req, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / req.getSize());
        return PageResponseDTO.<T>builder()
                .contents(contents)
                .page(req.getPage())
                .size(req.getSize())
                .totalCount(totalCount)
                .totalPages(Math.max(totalPages, 1))
                .hasNext(req.getPage() < totalPages)
                .hasPrev(req.getPage() > 1)
                .build();
    }
}
