package com.luckymarket.user.repository;

import com.luckymarket.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}