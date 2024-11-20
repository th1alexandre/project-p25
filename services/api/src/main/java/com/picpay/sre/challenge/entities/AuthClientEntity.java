package com.picpay.sre.challenge.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auths") 
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthClientEntity {
    @Id
    private String clientId;
    private String clientSecret;
}
