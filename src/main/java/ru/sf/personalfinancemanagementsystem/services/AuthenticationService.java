package ru.sf.personalfinancemanagementsystem.services;

import ru.sf.personalfinancemanagementsystem.domains.Credentials;
import ru.sf.personalfinancemanagementsystem.domains.Token;
import ru.sf.personalfinancemanagementsystem.domains.User;
import ru.sf.personalfinancemanagementsystem.domains.UserDataForRegister;


public interface AuthenticationService {

    UserDataForRegister register(Credentials credentials);
    Token issueToken(Credentials credentials);

}
