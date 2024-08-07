package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.dto.address.AddressUpdateDTO;
import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import com.ivan_degtev.documentaccounting2.repository.AddressEntityRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
        Future<AddressEntity> future = callExternalAPI(addressUpdateDTO);
        AddressEntity addressEntity;
        try {
            addressEntity = future.get();
            if (addressEntity != null) {
                addressEntityRepository.save(addressEntity);
                return addressEntity;
            } else {
                throw new RuntimeException("Ошибка получения данных из внешнего API");
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Ошибка при получении результата Future: {}", e.getMessage());
            // Откат только вложенной транзакции, но не основной
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Ошибка вызова внешнего API", e);
        }
    }

    @Async
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallback")
    @TimeLimiter(name = "externalApi")
    public Future<AddressEntity> callExternalAPI(AddressUpdateDTO addressUpdateDTO) {
        try {
            AddressEntity addressEntity = daDataServiceImpl.getAddressEntityOnRequest(addressUpdateDTO.getEnteredFullAddressForUpdate());
            return new AsyncResult<>(addressEntity);
        } catch (Exception e) {
            log.error("Ошибка при вызове внешнего API: {}", e.getMessage());
            return new AsyncResult<>(null);
        }
    }
}
