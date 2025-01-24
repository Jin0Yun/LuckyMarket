package com.luckymarket.product.repository;

import com.luckymarket.product.domain.Product;
import com.luckymarket.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> { }
