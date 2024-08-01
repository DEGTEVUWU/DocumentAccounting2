package com.ivan_degtev.documentaccounting2.model.interfaces;

import com.ivan_degtev.documentaccounting2.model.User;

import java.util.Set;

/**
 * Можно расширять классам, где есть привязка к уровням доступа для пользователей
 */
public interface Available {
    Boolean getPublicEntity();
    Set<User> getAvailableFor();
}
