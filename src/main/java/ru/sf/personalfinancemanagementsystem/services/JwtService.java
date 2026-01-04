package ru.sf.personalfinancemanagementsystem.services;

import ru.sf.personalfinancemanagementsystem.domains.UserDataForToken;
import ru.sf.personalfinancemanagementsystem.domains.Token;


public interface JwtService {

    Token issue(UserDataForToken userDataForToken);

}
