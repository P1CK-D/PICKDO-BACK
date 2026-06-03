package com.pickdo.backend.quote.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.pickdo.backend.quote.dto.TodayQuoteResponse;

@SpringBootTest
@ActiveProfiles("test")
class QuoteServiceTest {

    @Autowired
    private QuoteService quoteService;

    @Test
    void getTodayQuote() {
        TodayQuoteResponse response = quoteService.getTodayQuote();

        assertThat(response.id()).isNotNull();
        assertThat(response.content()).isNotBlank();
        assertThat(response.author()).isNotBlank();
    }
}
