package com.luckymarket.adapter.out.persistence.participation;

import com.luckymarket.domain.entity.participation.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByProductIdAndMemberId(Long productId, Long memberId);
    Optional<Participation> findByProductIdAndMemberId(Long productId, Long memberId);
}