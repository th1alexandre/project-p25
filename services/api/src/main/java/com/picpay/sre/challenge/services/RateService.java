package com.picpay.sre.challenge.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.picpay.sre.challenge.dtos.RateDto;
import com.picpay.sre.challenge.entities.RateEntity;
import com.picpay.sre.challenge.repositories.IRateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateService {
    private final IRateRepository rateRepository;

    public Long saveRate(RateDto rateDto) {
        RateEntity savedEntity = rateRepository.save(
                RateEntity.builder()
                        .name(rateDto.getName())
                        .description(rateDto.getDescription())
                        .rate(rateDto.getValue())
                        .build());

        return savedEntity.getId();
    }

    public RateDto getRate(Long id) {
        RateEntity rateEntity = rateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rate not found"));

        return RateDto.builder()
                .id(rateEntity.getId())
                .name(rateEntity.getName())
                .description(rateEntity.getDescription())
                .value(rateEntity.getRate())
                .build();
    }

    public List<RateDto> getAllRates() {
        return rateRepository.findAll().stream()
                .map(rateEntity -> RateDto.builder()
                        .id(rateEntity.getId())
                        .name(rateEntity.getName())
                        .description(rateEntity.getDescription())
                        .value(rateEntity.getRate())
                        .build())
                .collect(Collectors.toList());
    }

    public RateDto updateRate(Long id, RateDto rateDto) {
        RateEntity rateEntity = rateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rate not found"));

                
        rateEntity.setName(rateDto.getName() == null || rateDto.getName().isEmpty() ? rateEntity.getName() : rateDto.getName());
        rateEntity.setDescription(rateDto.getDescription() == null || rateDto.getDescription().isEmpty() ? rateEntity.getDescription() : rateDto.getDescription());
        rateEntity.setRate(rateDto.getValue() == null ? rateEntity.getRate() : rateDto.getValue());

        rateRepository.save(rateEntity);

        return RateDto.builder()
                .id(rateEntity.getId())
                .name(rateEntity.getName())
                .description(rateEntity.getDescription())
                .value(rateEntity.getRate())
                .build();
    }
}
