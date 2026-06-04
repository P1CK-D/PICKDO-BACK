package com.pickdo.backend.quote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pickdo.backend.quote.domain.Quote;

public interface QuoteRepository extends JpaRepository<Quote, Integer> {
}
