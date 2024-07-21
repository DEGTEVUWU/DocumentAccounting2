package com.ivan_degtev.documentaccounting2.model.interfaces;

import com.ivan_degtev.documentaccounting2.model.User;

import java.util.Set;

public interface Available {
    Boolean getPublicEntity();
    Set<User> getAvailableFor();
}
