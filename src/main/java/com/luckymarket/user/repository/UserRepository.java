package com.luckymarket.user.repository;

import com.luckymarket.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}