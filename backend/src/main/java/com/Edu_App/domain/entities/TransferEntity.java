package com.Edu_App.domain.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transfers")
public class TransferEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private AccountEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private AccountEntity receiver;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private CurrencyEntity currency;


    
}
