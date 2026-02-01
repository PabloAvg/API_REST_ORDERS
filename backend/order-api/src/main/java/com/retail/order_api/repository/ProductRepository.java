package com.retail.order_api.repository;

import com.retail.order_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // i.e (For reference) --> SELECT * FROM product WHERE LOWER(name) LIKE LOWER('%<name>%')
    List<Product> findByNameContainingIgnoreCase(String name);
}
