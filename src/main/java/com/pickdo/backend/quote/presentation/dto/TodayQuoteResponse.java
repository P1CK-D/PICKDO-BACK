package com.pickdo.backend.quote.presentation.dto;

public record TodayQuoteResponse(
        Integer quoteId,
        String content,
        String author
) {
}
