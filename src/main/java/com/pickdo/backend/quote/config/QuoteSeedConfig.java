package com.pickdo.backend.quote.config;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pickdo.backend.quote.domain.Quote;
import com.pickdo.backend.quote.repository.QuoteRepository;

@Configuration
public class QuoteSeedConfig {

    @Bean
    ApplicationRunner quoteSeedRunner(QuoteRepository quoteRepository) {
        return args -> {
            if (quoteRepository.count() > 0) {
                return;
            }

            quoteRepository.saveAll(List.of(
                    Quote.create("작은 선택이 오늘의 방향을 바꾼다.", "PickDo"),
                    Quote.create("지금 할 수 있는 가장 작은 좋은 일을 고르자.", "PickDo"),
                    Quote.create("좋은 하루는 완벽함보다 한 번의 실천에서 시작된다.", "PickDo"),
                    Quote.create("멈추고 고르는 순간, 습관은 다시 설계된다.", "PickDo"),
                    Quote.create("오늘의 에너지를 나를 돕는 일에 써보자.", "PickDo")
            ));
        };
    }
}
