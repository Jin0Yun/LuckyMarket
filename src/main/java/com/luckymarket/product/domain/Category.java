package com.luckymarket.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CATEGORY_CODE", unique = true)
    private String code;

    @Column(name = "CATEGORY_NAME")
    private String name;

    @Column(name = "PARENT_ID")
    private Long parent;
}
