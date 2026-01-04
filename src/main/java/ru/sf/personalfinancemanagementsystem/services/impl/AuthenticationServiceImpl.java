package ru.sf.personalfinancemanagementsystem.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sf.personalfinancemanagementsystem.domains.*;
import ru.sf.personalfinancemanagementsystem.entities.UserEntity;
import ru.sf.personalfinancemanagementsystem.exceptions.BadLoginOrPasswordException;
import ru.sf.personalfinancemanagementsystem.exceptions.UserAlreadyExistsException;
import ru.sf.personalfinancemanagementsystem.mappers.UserMapper;
import ru.sf.personalfinancemanagementsystem.repositories.UserRepository;
import ru.sf.personalfinancemanagementsystem.services.AuthenticationService;
import ru.sf.personalfinancemanagementsystem.services.JwtService;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;
    JwtService jwtService;


    @Override
    @Transactional
    public UserDataForRegister register(@NotNull Credentials credentials) {
        try {
            UserEntity user = UserEntity.builder()
                    .login(credentials.getLogin())
                    .passwordHash(passwordEncoder.encode(credentials.getPassword()))
                    .build();

            UserEntity savedUser = userRepository.save(user);

            return UserDataForRegister.builder()
                    .id(savedUser.getId())
                    .login(savedUser.getLogin())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(credentials.getLogin());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Token issueToken(@NotNull Credentials credentials) {
        User user = userRepository.findByLogin(credentials.getLogin())
                .orElseThrow(BadLoginOrPasswordException::new);

        if (!passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash())) {
            throw new BadLoginOrPasswordException();
        }

        UserDataForToken userDataForToken = UserDataForToken.builder()
                .id(user.getId())
                .login(user.getLogin())
                .build();

        return jwtService.issue(userDataForToken);
    }

}
