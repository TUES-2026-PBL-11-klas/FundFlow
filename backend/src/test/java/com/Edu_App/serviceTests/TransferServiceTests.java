package com.Edu_App.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.TransferEntity;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.services.AccountService;
import com.Edu_App.services.CurrencyService;
import com.Edu_App.services.TransferService;
import com.Edu_App.services.UserService;
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransferServiceTests {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private UserService userService;

    @Test
    public void testCreateTransfer() {

        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        UserEntity user2 = userService.createUser(TestData.CreateTestUserEntity2());

        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity sender = accountService.createAccount(TestData.CreateTestAccountEntity1(user1, currency));
        AccountEntity receiver = accountService.createAccount(TestData.CreateTestAccountEntity2(user2, currency));

        TransferEntity transfer = TestData.CreateTestTranferEntity1(sender, receiver, currency);

        TransferEntity saved = transferService.createTransfer(transfer);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSender().getId()).isEqualTo(sender.getId());
        assertThat(saved.getReceiver().getId()).isEqualTo(receiver.getId());
    }

    @Test
    public void testCreateTransferSameAccountThrows() {

        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));

        TransferEntity transfer = TestData.CreateTestTranferEntity1(account, account, currency);

        assertThrows(BadRequestException.class,
                () -> transferService.createTransfer(transfer));
    }

    @Test
    public void testExecuteTransfer() {

        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        UserEntity user2 = userService.createUser(TestData.CreateTestUserEntity2());

        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity sender = accountService.createAccount(TestData.CreateTestAccountEntity1(user1, currency));
        AccountEntity receiver = accountService.createAccount(TestData.CreateTestAccountEntity2(user2, currency));

        double senderInitial = sender.getBalance();
        double receiverInitial = receiver.getBalance();

        TransferEntity transfer = TestData.CreateTestTranferEntity1(sender, receiver, currency);
        TransferEntity saved = transferService.createTransfer(transfer);

        transferService.executeTransfer(saved.getId());

        AccountEntity updatedSender = accountService.findAccountById(sender.getId());
        AccountEntity updatedReceiver = accountService.findAccountById(receiver.getId());

        assertThat(updatedSender.getBalance())
                .isEqualTo(senderInitial - transfer.getAmount());

        assertThat(updatedReceiver.getBalance())
                .isEqualTo(receiverInitial + transfer.getAmount());
    }

    @Test
    public void testCreateTransferInsufficientFundsThrows() 
    {

        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        UserEntity user2 = userService.createUser(TestData.CreateTestUserEntity2());

        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity sender = accountService.createAccount(TestData.CreateTestAccountEntity1(user1, currency));
        AccountEntity receiver = accountService.createAccount(TestData.CreateTestAccountEntity2(user2, currency));

        TransferEntity transfer = TestData.CreateTestTranferEntity1(sender, receiver, currency);
        transfer.setAmount(sender.getBalance() + 1000);

        assertThrows(BadRequestException.class,
                () -> transferService.createTransfer(transfer));
    }

    @Test
    public void testFindTransferByIdThrows() {
        assertThrows(ResourceNotFoundException.class,
                () -> transferService.findTransferById(999));
    }

    @Test
    public void testGetTransfersForAccount() {

        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        UserEntity user2 = userService.createUser(TestData.CreateTestUserEntity2());

        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity sender = accountService.createAccount(TestData.CreateTestAccountEntity1(user1, currency));
        AccountEntity receiver = accountService.createAccount(TestData.CreateTestAccountEntity2(user2, currency));

        TransferEntity transfer = TestData.CreateTestTranferEntity1(sender, receiver, currency);
        transferService.createTransfer(transfer);

        var list = transferService.getAllTransfersForUser(sender.getId());

        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(1);
    }
}