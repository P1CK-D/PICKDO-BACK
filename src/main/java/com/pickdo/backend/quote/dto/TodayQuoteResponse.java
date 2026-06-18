package com.pickdo.backend.quote.dto;

public record TodayQuoteResponse(
        Integer quoteId,
        String content,
        String author
) {
}
