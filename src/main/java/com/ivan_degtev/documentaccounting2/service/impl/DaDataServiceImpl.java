package com.ivan_degtev.documentaccounting2.service.impl;

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

    /**
     * Настройка данного компонента через внедрения в него WebClient через его билдинг) для работы с внешними API
     */
    public DaDataServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(externalApiDaData).build();
    }

    /**
     * Метод для преобразоввания ответа от DaData в нужную сущность AddressEntity с заполнением всех нужных полей
     */
    public AddressEntity getAddressEntityOnRequest(String query) {
        Flux<Suggestion<Address>> fullResponse =
                getSuggestionsForAddressFromDaData(query);
        AddressEntity addressEntity = new AddressEntity();
        fullResponse
                .collectList()
                .map(suggestions -> {
                    if(!suggestions.isEmpty()) {
                        Address address = suggestions.get(0).getData();
                        addressEntity.setCountry(address.getCountry());
                        addressEntity.setCity(address.getCity());
                        addressEntity.setStreet(address.getStreet());
                        addressEntity.setHouse(address.getHouse());
                        addressEntity.setGeoLat(address.getGeoLat());
                        addressEntity.setGeoLon(address.getGeoLon());
                        addressEntity.setPostalCode(address.getPostalCode());
                        addressEntity.setFullAddress(addressEntity.getFullAddressByDataForOutput());
                    }
                    return addressEntity;
                }).block();
        return addressEntity;
    }

    /**
     * Утилитный метод выдает полный ответ от сервиса ДаДата
     */
    public Flux<Suggestion<Address>> getSuggestionsForAddressFromDaData(String query) {
        return client.suggestAddress(AddressRequestBuilder.create(query).build());
    }
}
