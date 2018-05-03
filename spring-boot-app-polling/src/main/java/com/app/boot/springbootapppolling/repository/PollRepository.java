package com.app.boot.springbootapppolling.repository;

import com.app.boot.springbootapppolling.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    Optional<Poll> findById(Long idpoll);

    Page<Poll> findByCreatedBy(Long idusers, Pageable pageable);

    long countByCreatedBy(Long idusers);

    List<Poll> findByIdpollIn(List<Long> idpolls);

    List<Poll> findByIdpollIn(List<Long> idpolls, Sort sort);
}
