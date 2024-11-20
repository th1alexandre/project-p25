package com.picpay.sre.challenge.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.picpay.sre.challenge.entities.LoanEntity;

@Repository
public interface ILoanRepository extends JpaRepository<LoanEntity, Long> {

    
} 

