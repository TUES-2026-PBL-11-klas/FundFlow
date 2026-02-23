package com.Edu_App.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.TransferEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.repositories.TransferRepository;
import com.Edu_App.services.AccountService;
import com.Edu_App.services.CurrencyService;
import com.Edu_App.services.TransferService;
@Service
public class TransferServiceImpl implements TransferService 
{
    private TransferRepository transferRepository;
    private AccountService accountService;
    private CurrencyService currencyService;
    public TransferServiceImpl(TransferRepository transferR, AccountService accountS, CurrencyService currencyS)
    {
        this.transferRepository = transferR;
        this.accountService = accountS;
        this.currencyService = currencyS;
    }

    @Override
    public TransferEntity createTransfer(TransferEntity transferEntity)
    {
        validateTransferAmount(transferEntity.getSender().getId(), 
                                transferEntity.getAmount(), 
                                transferEntity.getCurrency().getId());
        AccountEntity sender = accountService.findActiveAccountById(transferEntity.getSender().getId());
        AccountEntity receiver = accountService.findActiveAccountById(transferEntity.getReceiver().getId());
        if(sender.equals(receiver))
        {
            throw new BadRequestException("the receiver must be different from the sender");
        }
        CurrencyEntity currency = currencyService.findCurrencyById(transferEntity.getCurrency().getId());
        transferEntity.setSender(sender);
        transferEntity.setReceiver(receiver);
        transferEntity.setCurrency(currency);

        return this.transferRepository.save(transferEntity);
    }


    @Override
    public TransferEntity findTransferById(Integer id)
    {
        Optional<TransferEntity> transfer = this.transferRepository.findById(id);
        if(!transfer.isPresent())
        {
            throw new ResourceNotFoundException("this transfer does not exist");
        }
        validateTransferAmount(transfer.get().getSender().getId(), 
                                transfer.get().getAmount(),
                                transfer.get().getCurrency().getId());
        return transfer.get();
    }

    @Override
    public List<TransferEntity> getAllTransfers()
    {
        Iterable<TransferEntity> transfers = this.transferRepository.findAll();
        List<TransferEntity> transferList = new ArrayList<>();
        transfers.forEach(transferList::add);
        return transferList;
    }

    @Override
    public List<TransferEntity> getAllTransfersForUser(Integer userId)
    {
        this.accountService.findActiveAccountById(userId);
        return this.transferRepository.getTrasnfersWithThisAccount(userId);
    }

    @Override
    public List<TransferEntity> getTransfersSentByUser(Integer userId)
    {
        this.accountService.findActiveAccountById(userId);
        return this.transferRepository.getTrasnfersWithThisSender(userId);
    }

    @Override
    public List<TransferEntity> getTransfersReceivedByUser(Integer userId)
    {
        this.accountService.findActiveAccountById(userId);
        return this.transferRepository.getTrasnfersWithThisReceiver(userId);
    }

    @Override
    public void validateTransferAmount(Integer senderId, double amount, Integer currencyId)
    {
        if(amount <= 0)
        {
            throw new BadRequestException("Amount cannot be negative");
        }
        AccountEntity sender = this.accountService.findActiveAccountById(senderId);
        double amountInSenderCurrency = this.currencyService.convertAmount(amount, currencyId,  sender.getCurrency().getId());
        if(sender.getBalance() < amountInSenderCurrency)
        {
            throw new BadRequestException("Amount cannot be greater than the sender's balance");
        }

    }

    @Transactional
    @Override
    public void executeTransfer(Integer transferId)
    {
        TransferEntity transfer = this.findTransferById(transferId);
        AccountEntity sender = transfer.getSender();
        AccountEntity receiver = transfer.getReceiver();
        CurrencyEntity transferCurrency = transfer.getCurrency();
        double amount = transfer.getAmount();
        validateTransferAmount(sender.getId(), amount, transferCurrency.getId());
        double amountFromSender = this.currencyService.convertAmount(amount, 
                                                                    transferCurrency.getId(), 
                                                                    sender.getCurrency().getId());
        double amountToReceiver = currencyService.convertAmount(amount,
                                                                transferCurrency.getId(),
                                                                receiver.getCurrency().getId());
        this.accountService.withdrawFromAccount(sender.getId(), amountFromSender);
        this.accountService.depositInAccount(receiver.getId(), amountToReceiver);
        
        
    }
    
}
