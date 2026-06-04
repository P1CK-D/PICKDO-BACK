package com.pickdo.backend.quote.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickdo.backend.quote.domain.Quote;
import com.pickdo.backend.quote.dto.TodayQuoteResponse;
import com.pickdo.backend.quote.repository.QuoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final QuoteRepository quoteRepository;

    public TodayQuoteResponse getTodayQuote() {
        List<Quote> quotes = quoteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (quotes.isEmpty()) {
            throw new IllegalStateException("등록된 명언이 없습니다.");
        }

        int quoteIndex = Math.floorMod(LocalDate.now(SEOUL_ZONE).toEpochDay(), quotes.size());
        Quote quote = quotes.get(quoteIndex);

        return new TodayQuoteResponse(quote.getId(), quote.getContent(), quote.getAuthor());
    }
}
