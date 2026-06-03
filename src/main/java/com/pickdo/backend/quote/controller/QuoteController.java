package com.pickdo.backend.quote.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pickdo.backend.global.response.ResponseEnvelope;
import com.pickdo.backend.quote.dto.TodayQuoteResponse;
import com.pickdo.backend.quote.service.QuoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("/today")
    public ResponseEnvelope<TodayQuoteResponse> getTodayQuote() {
        return ResponseEnvelope.success(quoteService.getTodayQuote());
    }
}
