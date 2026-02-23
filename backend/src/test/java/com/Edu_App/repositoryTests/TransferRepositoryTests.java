package com.Edu_App.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.TransferEntity;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.repositories.AccountRepository;
import com.Edu_App.repositories.CurrencyRepository;
import com.Edu_App.repositories.TransferRepository;
import com.Edu_App.repositories.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransferRepositoryTests {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public TransferRepositoryTests(TransferRepository transferR, AccountRepository accountR, 
                                 UserRepository userR, CurrencyRepository currencyR) {
        this.transferRepository = transferR;
        this.accountRepository = accountR;
        this.userRepository = userR;
        this.currencyRepository = currencyR;
    }

    @Test
    public void testSaveAndFindTransfer() {
        CurrencyEntity bgn = this.currencyRepository.save(TestData.CreateTestCurrencyEntity1());
        UserEntity senderUser = this.userRepository.save(TestData.CreateTestUserEntity1());
        UserEntity receiverUser = this.userRepository.save(TestData.CreateTestUserEntity2());
        AccountEntity senderAcc = this.accountRepository.save(TestData.CreateTestAccountEntity1(senderUser, bgn));
        AccountEntity receiverAcc = this.accountRepository.save(TestData.CreateTestAccountEntity2(receiverUser, bgn));

        TransferEntity transfer = TransferEntity.builder()
                .sender(senderAcc)
                .receiver(receiverAcc)
                .amount(150.0)
                .currency(bgn)
                .build();

        TransferEntity savedTransfer = this.transferRepository.save(transfer);

        Optional<TransferEntity> result = this.transferRepository.findById(savedTransfer.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualTo(150.0);
        assertThat(result.get().getSender().getIban()).isEqualTo(senderAcc.getIban());
        assertThat(result.get().getReceiver().getIban()).isEqualTo(receiverAcc.getIban());
    }

    @Test
    public void testDeleteTransfer() {
        CurrencyEntity curr = this.currencyRepository.save(TestData.CreateTestCurrencyEntity1());
        UserEntity user = this.userRepository.save(TestData.CreateTestUserEntity1());
        AccountEntity acc1 = this.accountRepository.save(TestData.CreateTestAccountEntity1(user, curr));
        AccountEntity acc2 = this.accountRepository.save(TestData.CreateTestAccountEntity2(user, curr));

        TransferEntity transfer = this.transferRepository.save(TransferEntity.builder()
                .sender(acc1).receiver(acc2).amount(50.0).currency(curr).build());

        Integer transferId = transfer.getId();
        
        this.transferRepository.deleteById(transferId);
        
        assertThat(this.transferRepository.findById(transferId)).isEmpty();
        assertThat(this.accountRepository.existsById(acc1.getId())).isTrue();
    }
}