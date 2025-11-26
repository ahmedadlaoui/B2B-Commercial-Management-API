package com.example.smartshop.repository;

import com.example.smartshop.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    @Query("SELECT p FROM PromoCode p WHERE p.code = :code AND p.active = true")
    Optional<PromoCode> findByCodeAndActive(String code);
}
