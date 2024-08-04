package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import com.kuliginstepan.dadata.client.domain.Suggestion;
import com.kuliginstepan.dadata.client.domain.address.Address;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface DaDataService {
    Flux<Suggestion<Address>> getSuggestionsForAddressFromDaData(String query);
    AddressEntity getAddressEntityOnRequest(String query);
}
