package com.pickdo.backend.quote.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pickdo.backend.global.response.ResponseEnvelope;
import com.pickdo.backend.quote.application.QuoteService;
import com.pickdo.backend.quote.presentation.dto.TodayQuoteResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("/today")
    public ResponseEnvelope<TodayQuoteResponse> getTodayQuote() {
        return ResponseEnvelope.success(quoteService.getTodayQuote());
    }
}
