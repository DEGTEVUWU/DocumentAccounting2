package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import org.springframework.stereotype.Service;

@Service
public interface AccessService {
    boolean currentUserIsAuthor(Authorable entity);
}
