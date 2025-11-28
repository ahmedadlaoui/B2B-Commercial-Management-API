package com.example.smartshop.controller;

import com.example.smartshop.dto.promocode.PromoCodeCreateRequest;
import com.example.smartshop.dto.promocode.PromoCodeDTO;
import com.example.smartshop.service.PromoCodeService;
import com.example.smartshop.util.AuthorizationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promocodes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPromoCode(
            @Valid @RequestBody PromoCodeCreateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        PromoCodeDTO promoCode = promoCodeService.createPromoCode(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Promo code created successfully");
        response.put("promoCode", promoCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivatePromoCode(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        PromoCodeDTO promoCode = promoCodeService.deactivatePromoCode(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Promo code deactivated successfully");
        response.put("promoCode", promoCode);

        return ResponseEntity.ok(response);
    }
}
