package com.luckymarket.adapter.out.persistence.product;

import com.luckymarket.domain.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByMemberId(Long memberId);
    List<Product> findByParticipations_Member_Id(Long memberId);
}
