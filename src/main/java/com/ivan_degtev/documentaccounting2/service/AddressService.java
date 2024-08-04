package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.address.AddressUpdateDTO;
import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {
    AddressEntity updateAddress(AddressUpdateDTO addressUpdateDTO);
}
