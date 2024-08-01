package com.ivan_degtev.documentaccounting2.model.interfaces;

import com.ivan_degtev.documentaccounting2.model.User;

/**
 * Можно расширять классам, где есть привязка к юзеру, как к автору
 */
public interface Authorable {
    User getAuthor();
}
