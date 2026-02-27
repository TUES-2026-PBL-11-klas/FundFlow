package com.Edu_App.DTOs;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;
    private String currencyCode;
}
