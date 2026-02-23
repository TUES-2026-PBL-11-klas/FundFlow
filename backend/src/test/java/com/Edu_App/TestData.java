package com.Edu_App;



import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.TransferEntity;
import com.Edu_App.domain.entities.UserEntity;

public final class TestData {
    private TestData(){}

    public static UserEntity CreateTestUserEntity1()
    {
        return UserEntity.builder().username(" IMRosen")
                                                        .email("eric_rosen@gmail.com")
                                                        .hashPassword("weafw3")
                                                        .build();
    }

    public static UserEntity CreateTestUserEntity2()
    {
        return UserEntity.builder().username("GothamChess")
                                                        .email("levi_rozman@gmail.com")
                                                        .hashPassword("u6tddgd")
                                                        .build();
    }

    public static AccountEntity CreateTestAccountEntity1(UserEntity userE, CurrencyEntity currencyE)
    {
        return  AccountEntity.builder()
            .iban("BG123456789")
            .balance(1000.0)
            .owner(userE)
            .currency(currencyE)
            .build();
    }

    public static AccountEntity CreateTestAccountEntity2(UserEntity userE, CurrencyEntity currencyE)
    {
        return  AccountEntity.builder()
            .iban("BG987654321")
            .balance(1500.40)
            .owner(userE)
            .currency(currencyE)
            .build();
    }

    public static CurrencyEntity CreateTestCurrencyEntity1()
    {
        return CurrencyEntity.builder()
            .code("BGN")
            .exchangeRate(1.0)
            .build();
    }

    public static CurrencyEntity CreateTestCurrencyEntity2()
    {
        return CurrencyEntity.builder()
            .code("EUR")
            .exchangeRate(1.97)
            .build();
    }

    public static TransferEntity CreateTestTranferEntity1(AccountEntity sender, AccountEntity reciever, CurrencyEntity currencyE)
    {
        return TransferEntity.builder()
                            .amount(500.0)
                            .sender(sender)
                            .receiver(reciever)
                            .currency(currencyE).build();
    }

    public static TransferEntity CreateTestTranferEntity2(AccountEntity sender, AccountEntity reciever, CurrencyEntity currencyE)
    {
        return TransferEntity.builder()
                            .amount(250.0)
                            .sender(sender)
                            .receiver(reciever)
                            .currency(currencyE).build();
    }

}
