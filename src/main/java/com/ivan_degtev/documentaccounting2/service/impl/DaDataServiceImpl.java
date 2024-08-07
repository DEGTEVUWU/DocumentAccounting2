package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.mapper.AddressMapper;
import com.ivan_degtev.documentaccounting2.service.DaDataService;
import com.kuliginstepan.dadata.client.DadataClient;
import com.kuliginstepan.dadata.client.domain.Suggestion;
import com.kuliginstepan.dadata.client.domain.address.Address;
import com.kuliginstepan.dadata.client.domain.address.AddressRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import com.ivan_degtev.documentaccounting2.model.AddressEntity;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class DaDataServiceImpl implements DaDataService {

    /**
     * Базовый url-запрос на Dadata для получения поолной информации о гео-позиции
     * на основе переданного адреса в произвольной форме
     */
    @Value("${externalApiDaData}")
    private String externalApiDaData;

    /**
     * Поле клиента зависимости DadataClient - внешнего API
     */
    @Autowired
    private DadataClient client;

    private final WebClient webClient;
    private final AddressMapper addressMapper;

    /**
     * Настройка данного компонента через внедрения в него WebClient через его билдинг) для работы с внешними API
     */
    public DaDataServiceImpl(WebClient.Builder webClientBuilder, AddressMapper addressMapper) {
        this.webClient = webClientBuilder.baseUrl(externalApiDaData).build();
        this.addressMapper = addressMapper;
    }

    /**
     * Метод для преобразоввания ответа от DaData в нужную сущность AddressEntity с заполнением всех нужных полей
     */
    public AddressEntity getAddressEntityOnRequest(String query) {
        Flux<Suggestion<Address>> fullResponse =
                getSuggestionsForAddressFromDaData(query);
        AtomicReference<AddressEntity> addressEntity = new AtomicReference<>(new AddressEntity());
        fullResponse
                .collectList()
                .map(suggestions -> {
                    if(!suggestions.isEmpty()) {
                        Address address = suggestions.get(0).getData();
                        addressEntity.set(addressMapper.mapToAddressEntity(address));
                        addressEntity.get().setFullAddress(addressEntity.get().getFullAddressByDataForOutput());
                    }
                    return addressEntity;
                }).block();
        return addressEntity.get();
    }

    /**
     * Утилитный метод выдает полный ответ от сервиса ДаДата
     */
    public Flux<Suggestion<Address>> getSuggestionsForAddressFromDaData(String query) {
        return client.suggestAddress(AddressRequestBuilder.create(query).build());
    }
}
