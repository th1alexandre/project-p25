package com.picpay.sre.challenge.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentId implements Serializable {
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "installment_number")
    private Integer installmentNumber;
}
