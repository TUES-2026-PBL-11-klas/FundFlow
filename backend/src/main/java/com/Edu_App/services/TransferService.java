package com.Edu_App.services;

import java.util.List;

import com.Edu_App.domain.entities.TransferEntity;

public interface TransferService {
    
    public TransferEntity createTransfer(TransferEntity transferEntity);
    public TransferEntity findTransferById(Integer id);
    public List<TransferEntity> getAllTransfers();
    public List<TransferEntity> getAllTransfersForUser(Integer userId);
    public List<TransferEntity> getTransfersSentByUser(Integer userId);
    public List<TransferEntity> getTransfersReceivedByUser(Integer userId);
    public void validateTransferAmount(Integer senderId, double amount, Integer currencyId);
    public void executeTransfer(Integer transferId);
}
