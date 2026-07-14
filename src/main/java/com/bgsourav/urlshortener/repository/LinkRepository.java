package com.bgsourav.urlshortener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bgsourav.urlshortener.domain.Link;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByCode(String code);

    List<Link> findByLongUrl(String longUrl);
}
