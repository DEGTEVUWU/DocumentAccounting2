package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.dto.address.AddressUpdateDTO;
import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import com.ivan_degtev.documentaccounting2.repository.AddressEntityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@AllArgsConstructor
@Slf4j
public class AddressServiceImpl {
    private DaDataServiceImpl daDataServiceImpl;
    private AddressEntityRepository addressEntityRepository;


    /**
     * Используется вложенная транзакция, чтоб защититься от возможных проблем в работе внешнего API
     * и откатить только неудавшиеся изменения AddressEntity, не затронул другие сущности(напр. юзера),
     * которые меняются и вызвают этот сервис
     */
    @Transactional(propagation = Propagation.NESTED, transactionManager = "transactionManager")
    public AddressEntity updateAddress(AddressUpdateDTO addressUpdateDTO) {
        try {
            AddressEntity addressEntity = daDataServiceImpl.getAddressEntityOnRequest(
                    addressUpdateDTO.getEnteredFullAddressForUpdate());
            addressEntityRepository.save(addressEntity);
            return addressEntity;
        } catch (Exception e) {
            log.error("Error while updating address: {}", e.getMessage());
            // Откат только вложенной транзакции, но не основной
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
