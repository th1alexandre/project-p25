package com.picpay.sre.challenge.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpay.sre.challenge.dtos.AuthClientDto;
import com.picpay.sre.challenge.dtos.AuthTokenDto;
import com.picpay.sre.challenge.services.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<AuthTokenDto> getToken(@RequestBody AuthClientDto authClientDto) {
        AuthTokenDto token = jwtService.generateToken(authClientDto);
        return ResponseEntity.ok(token);
    }
}
