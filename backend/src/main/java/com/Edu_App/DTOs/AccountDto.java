package com.Edu_App.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private String iban;
    private double balance;
    private String currencyCode; 
    private String status;
}