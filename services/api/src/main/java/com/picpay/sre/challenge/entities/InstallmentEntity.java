package com.picpay.sre.challenge.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "installments") 
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentEntity {
    @EmbeddedId
    private InstallmentId id;

    @ManyToOne
    @MapsId("loanId")
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    private Double value;
    private LocalDate dueDate;
}
