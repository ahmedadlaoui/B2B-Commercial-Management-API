package com.example.smartshop.service.impl;

import com.example.smartshop.dto.promocode.PromoCodeCreateRequest;
import com.example.smartshop.dto.promocode.PromoCodeDTO;
import com.example.smartshop.entity.PromoCode;
import com.example.smartshop.exception.DuplicateResourceException;
import com.example.smartshop.exception.ResourceNotFoundException;
import com.example.smartshop.mapper.PromoCodeMapper;
import com.example.smartshop.repository.PromoCodeRepository;
import com.example.smartshop.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    @Transactional
    public PromoCodeDTO createPromoCode(PromoCodeCreateRequest request) {
        if (promoCodeRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Promo code already exists: " + request.getCode());
        }

        PromoCode promoCode = PromoCode.builder()
                .code(request.getCode().toUpperCase())
                .discountPercent(request.getDiscountPercent())
                .expirationDate(request.getExpirationDate())
                .active(true)
                .build();

        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
        return promoCodeMapper.toDTO(savedPromoCode);
    }

    @Override
    @Transactional
    public PromoCodeDTO deactivatePromoCode(Long id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found with ID: " + id));

        promoCode.setActive(false);
        PromoCode updatedPromoCode = promoCodeRepository.save(promoCode);

        return promoCodeMapper.toDTO(updatedPromoCode);
    }
}
