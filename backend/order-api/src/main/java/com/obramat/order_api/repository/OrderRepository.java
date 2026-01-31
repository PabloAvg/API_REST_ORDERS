package com.obramat.order_api.repository;

import com.obramat.order_api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
           select distinct o
           from Order o
           left join fetch o.details d
           left join fetch d.product
           where o.id = :id
           """)
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}
