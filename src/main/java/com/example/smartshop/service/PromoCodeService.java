package com.example.smartshop.service;

import com.example.smartshop.dto.promocode.PromoCodeCreateRequest;
import com.example.smartshop.dto.promocode.PromoCodeDTO;

public interface PromoCodeService {

    PromoCodeDTO createPromoCode(PromoCodeCreateRequest request);

    PromoCodeDTO deactivatePromoCode(Long id);
}
