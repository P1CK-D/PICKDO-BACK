package com.pickdo.backend.quote.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pickdo.backend.quote.domain.Quote;

public interface QuoteRepository extends JpaRepository<Quote, Integer> {
}
