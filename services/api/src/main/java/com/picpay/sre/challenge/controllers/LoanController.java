package com.picpay.sre.challenge.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpay.sre.challenge.dtos.LoanDto;
import com.picpay.sre.challenge.services.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/loans")
    public ResponseEntity<Long> saveLoan(@RequestBody LoanDto loanDto) {
        Long id = loanService.saveLoan(loanDto);
        return ResponseEntity.created(null).body(id);
    }

    @GetMapping("/loans/{id}")
    public ResponseEntity<LoanDto> getLoan(@PathVariable("id") Long id) {
        LoanDto loan = loanService.getLoan(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanDto>> getLoans() {
        List<LoanDto> loans = loanService.getLoans();
        return ResponseEntity.ok(loans);
    }
}
