package com.luckymarket.adapter.out.persistence.participation;

import com.luckymarket.domain.entity.participation.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> { }