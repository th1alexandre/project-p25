package com.picpay.sre.challenge.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.picpay.sre.challenge.dtos.InstallmentDto;
import com.picpay.sre.challenge.dtos.LoanDto;
import com.picpay.sre.challenge.dtos.RateDto;
import com.picpay.sre.challenge.entities.InstallmentEntity;
import com.picpay.sre.challenge.entities.InstallmentId;
import com.picpay.sre.challenge.entities.LoanEntity;
import com.picpay.sre.challenge.entities.RateEntity;

import com.picpay.sre.challenge.repositories.ILoanRepository;
import com.picpay.sre.challenge.repositories.IRateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final ILoanRepository loanRepository;
    private final IRateRepository rateRepository;

    public List<LoanDto> getLoans() {
        return loanRepository.findAll().stream()
                .map(loan -> LoanDto.builder()
                        .id(loan.getId())
                        .name(loan.getName())
                        .description(loan.getDescription())
                        .value(loan.getValue())
                        .startDate(loan.getStartDate())
                        .installmentsNumber(loan.getInstallments().size())
                        .installments(loan.getInstallments().stream()
                                .map(installment -> InstallmentDto.builder()
                                        .installmentNumber(installment.getId().getInstallmentNumber())
                                        .value(installment.getValue())
                                        .dueDate(installment.getDueDate())
                                        .build())
                                .toList())
                        .rate(RateDto.builder()
                                .id(loan.getRate().getId())
                                .name(loan.getRate().getName())
                                .description(loan.getRate().getDescription())
                                .value(loan.getRate().getRate())
                                .build())
                        .build())
                .toList();
    }

    public LoanDto getLoan(Long id) {
        LoanEntity loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        List<InstallmentDto> installments = loan.getInstallments().stream()
                .map(installment -> InstallmentDto.builder()
                        .installmentNumber(installment.getId().getInstallmentNumber())
                        .value(installment.getValue())
                        .dueDate(installment.getDueDate())
                        .build())
                .toList();

        return LoanDto.builder()
                .id(loan.getId())
                .name(loan.getName())
                .description(loan.getDescription())
                .value(loan.getValue())
                .startDate(loan.getStartDate())
                .installmentsNumber(loan.getInstallments().size())
                .installments(installments)
                .rate(RateDto.builder()
                        .id(loan.getRate().getId())
                        .name(loan.getRate().getName())
                        .description(loan.getRate().getDescription())
                        .value(loan.getRate().getRate())
                        .build())
                .build();
    }

    public Long saveLoan(LoanDto loanDto) {
        RateEntity rate = rateRepository.findById(loanDto.getRate().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rate not found"));

        LoanEntity savedEntity = loanRepository.save(
                LoanEntity.builder()
                        .name(loanDto.getName())
                        .description(loanDto.getDescription())
                        .value(loanDto.getValue())
                        .startDate(loanDto.getStartDate())
                        .rate(rate)
                        .build());

        Double installmentValue = loanDto.getValue() / loanDto.getInstallmentsNumber();
        installmentValue = installmentValue + (installmentValue * rate.getRateValue());

        List<InstallmentEntity> installments = new ArrayList<>();

        for (int i = 1; i <= loanDto.getInstallmentsNumber(); i++) {
            installments.add(InstallmentEntity.builder()
                    .id(InstallmentId.builder()
                            .loanId(savedEntity.getId())
                            .installmentNumber(i)
                            .build())
                    .loan(savedEntity)
                    .value(installmentValue)
                    .dueDate(loanDto.getStartDate().plusMonths(i))
                    .build());
        }

        savedEntity.setInstallments(installments);
        loanRepository.save(savedEntity);
        return savedEntity.getId();
    }
}
