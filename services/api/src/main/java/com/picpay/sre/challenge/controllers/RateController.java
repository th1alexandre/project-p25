package com.picpay.sre.challenge.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpay.sre.challenge.dtos.RateDto;
import com.picpay.sre.challenge.services.RateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class RateController {
    
    private final RateService rateService;

    @PostMapping("/rates")
    public ResponseEntity<Long> saveRate(@RequestBody RateDto rateDto) {
        Long id = rateService.saveRate(rateDto);
        return ResponseEntity.created(null).body(id);
    }

    @GetMapping("/rates/{id}")
    public ResponseEntity<RateDto> getRate(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rateService.getRate(id));
    }

    @GetMapping("/rates")
    public ResponseEntity<List<RateDto>> getAllRates() {
        return ResponseEntity.ok(rateService.getAllRates());
    }

    @PutMapping("/rates/{id}")
    public ResponseEntity<RateDto> updateRate(@PathVariable("id") Long id, @RequestBody RateDto rateDto) {
        return ResponseEntity.ok(rateService.updateRate(id, rateDto));
    }
}
