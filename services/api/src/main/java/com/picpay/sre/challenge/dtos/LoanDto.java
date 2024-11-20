package com.picpay.sre.challenge.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {
    private Long id;
    private String name;
    private String description;
    private Double value;
    private Integer installmentsNumber;
    private List<InstallmentDto> installments;
    private LocalDate startDate;
    private RateDto rate;
}

