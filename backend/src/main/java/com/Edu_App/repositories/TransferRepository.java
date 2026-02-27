package com.Edu_App.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.TransferEntity;

public interface TransferRepository extends CrudRepository<TransferEntity, Integer>{
    @Query("SELECT t FROM TransferEntity t WHERE t.sender.id = :accountId OR t.receiver.id = :accountId")
    public List<TransferEntity> getTrasnfersWithThisAccount(Integer accountId);

    @Query("SELECT t FROM TransferEntity t WHERE t.sender.id = :accountId")
    public List<TransferEntity> getTrasnfersWithThisSender(Integer accountId);

    @Query("SELECT t FROM TransferEntity t WHERE t.receiver.id = :accountId")
    public List<TransferEntity> getTrasnfersWithThisReceiver(Integer accountId);
}
