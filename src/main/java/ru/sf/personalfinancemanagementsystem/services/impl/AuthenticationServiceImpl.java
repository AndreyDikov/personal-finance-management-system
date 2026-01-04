package ru.sf.personalfinancemanagementsystem.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sf.personalfinancemanagementsystem.dto.requests.CredentialsRequestDto;
import ru.sf.personalfinancemanagementsystem.entities.UserEntity;
import ru.sf.personalfinancemanagementsystem.repositories.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserEntity register(CredentialsRequestDto req) {
        if (userRepo.existsByLogin(req.login())) {
            throw new UserAlreadyExistsException(req.login());
        }

        UserEntity user = UserEntity.builder()
                .login(req.login())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();

        try {
            return userRepo.save(user); // UUID будет сгенерен Hibernate’ом
        } catch (DataIntegrityViolationException e) {
            // на случай гонки/параллельной регистрации
            throw new UserAlreadyExistsException(req.login());
        }
    }

    @Transactional(readOnly = true)
    public JwtService.Token issueToken(CredentialsRequestDto req) {
        UserEntity user = userRepo.findByLogin(req.login())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return jwtService.issue(user.getId(), user.getLogin());
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String login) {
            super("Login already exists: " + login);
        }
    }
}