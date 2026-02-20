package com.Edu_App.domain.entities;



import jakarta.persistence.Entity;
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
@Table(name = "accounts")
@Entity
public class AccountEntity {
    
    @Id
    private String iban;
    
    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;
    

    @ManyToOne
    @JoinColumn(name = "currency_code")
    private CurrencyEntity currency;
}
