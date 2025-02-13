package com.luckymarket.domain.entity.participation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.user.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @Column(nullable = false)
    private LocalDateTime participatedAt;

    @PrePersist
    public void prePersist() {
        this.participatedAt = LocalDateTime.now();
    }
}