package com.pickdo.backend.quote.dto;

public record TodayQuoteResponse(
        Integer id,
        String content,
        String author
) {
}
