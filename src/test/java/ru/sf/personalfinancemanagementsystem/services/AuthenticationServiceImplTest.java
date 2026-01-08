package ru.sf.personalfinancemanagementsystem.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sf.personalfinancemanagementsystem.domains.*;
import ru.sf.personalfinancemanagementsystem.entities.UserEntity;
import ru.sf.personalfinancemanagementsystem.exceptions.BadLoginOrPasswordException;
import ru.sf.personalfinancemanagementsystem.exceptions.UserAlreadyExistsException;
import ru.sf.personalfinancemanagementsystem.exceptions.UserNotFoundException;
import ru.sf.personalfinancemanagementsystem.repositories.UserRepository;
import ru.sf.personalfinancemanagementsystem.services.impl.AuthenticationServiceImpl;
import ru.sf.personalfinancemanagementsystem.services.impl.JwtServiceImpl;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthenticationServiceImplTest {

    private static final String LOGIN = "bogdan";
    private static final String PASSWORD = "StrongPass123";
    private static final String BAD_PASSWORD = "WrongPass";
    private static final String PASSWORD_HASH = "$bcrypt$hash";

    @Mock UserRepository userRepository;
    @Mock JwtServiceImpl jwtService;
    @Mock PasswordEncoder passwordEncoder;

    @Captor ArgumentCaptor<UserEntity> userEntityCaptor;
    @Captor ArgumentCaptor<UserDataForToken> userDataForTokenCaptor;

    @InjectMocks AuthenticationServiceImpl service;


    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("Успешная регистрация: сохраняет пользователя и возвращает id + login")
        void shouldSaveUserAndReturnIdAndLogin() {
            Credentials credentials = mock(Credentials.class);
            when(credentials.getLogin()).thenReturn(LOGIN);
            when(credentials.getPassword()).thenReturn(PASSWORD);

            when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD_HASH);

            UUID savedId = UUID.randomUUID();
            UserEntity savedUser = UserEntity.builder()
                    .id(savedId)
                    .login(LOGIN)
                    .passwordHash(PASSWORD_HASH)
                    .build();

            when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(savedUser);

            UserDataForRegister result = service.register(credentials);

            assertThat(result.getId()).isEqualTo(savedId);
            assertThat(result.getLogin()).isEqualTo(LOGIN);

            verify(userRepository).saveAndFlush(userEntityCaptor.capture());
            UserEntity toSave = userEntityCaptor.getValue();

            assertThat(toSave.getLogin()).isEqualTo(LOGIN);
            assertThat(toSave.getPasswordHash()).isEqualTo(PASSWORD_HASH);

            verify(passwordEncoder).encode(PASSWORD);
            verifyNoInteractions(jwtService);
        }


        @Test
        @DisplayName("Повторная регистрация: при нарушении unique кидает UserAlreadyExistsException")
        void shouldThrowUserAlreadyExistsWhenUniqueConstraintViolated() {
            Credentials credentials = mock(Credentials.class);
            when(credentials.getLogin()).thenReturn(LOGIN);
            when(credentials.getPassword()).thenReturn(PASSWORD);

            when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD_HASH);

            when(userRepository.saveAndFlush(any(UserEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

            assertThatThrownBy(() -> service.register(credentials))
                    .isInstanceOf(UserAlreadyExistsException.class);

            verify(passwordEncoder).encode(PASSWORD);
            verify(userRepository).saveAndFlush(any(UserEntity.class));
            verifyNoInteractions(jwtService);
        }

    }


    @Nested
    @DisplayName("issueToken()")
    class IssueToken {

        @Test
        @DisplayName("Пользователь не найден: кидает UserNotFoundException")
        void shouldThrowUserNotFoundWhenUserNotFound() {
            Credentials credentials = mock(Credentials.class);
            when(credentials.getLogin()).thenReturn(LOGIN);

            when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.issueToken(credentials))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository).findByLogin(LOGIN);
            verifyNoInteractions(passwordEncoder, jwtService);
        }


        @Test
        @DisplayName("Неверный пароль: кидает BadLoginOrPasswordException и не вызывает выпуск JWT")
        void shouldThrowBadLoginOrPasswordWhenPasswordInvalid() {
            Credentials credentials = mock(Credentials.class);
            when(credentials.getLogin()).thenReturn(LOGIN);
            when(credentials.getPassword()).thenReturn(BAD_PASSWORD);

            User user = mock(User.class);
            when(user.getPasswordHash()).thenReturn(PASSWORD_HASH);

            when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(BAD_PASSWORD, PASSWORD_HASH)).thenReturn(false);

            assertThatThrownBy(() -> service.issueToken(credentials))
                    .isInstanceOf(BadLoginOrPasswordException.class);

            verify(userRepository).findByLogin(LOGIN);
            verify(passwordEncoder).matches(BAD_PASSWORD, PASSWORD_HASH);
            verify(jwtService, never()).issue(any());
        }


        @Test
        @DisplayName("Успешный логин: выдаёт токен и вызывает JwtService.issue с правильными данными")
        void shouldIssueTokenWhenCredentialsValid() {
            Credentials credentials = mock(Credentials.class);
            when(credentials.getLogin()).thenReturn(LOGIN);
            when(credentials.getPassword()).thenReturn(PASSWORD);

            UUID userId = UUID.randomUUID();

            User user = mock(User.class);
            when(user.getId()).thenReturn(userId);
            when(user.getLogin()).thenReturn(LOGIN);
            when(user.getPasswordHash()).thenReturn(PASSWORD_HASH);

            when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, PASSWORD_HASH)).thenReturn(true);

            Token token = mock(Token.class);
            when(jwtService.issue(any(UserDataForToken.class))).thenReturn(token);

            Token result = service.issueToken(credentials);

            assertThat(result).isSameAs(token);

            verify(jwtService).issue(userDataForTokenCaptor.capture());
            UserDataForToken data = userDataForTokenCaptor.getValue();

            assertThat(data.getId()).isEqualTo(userId);
            assertThat(data.getLogin()).isEqualTo(LOGIN);

            verify(userRepository).findByLogin(LOGIN);
            verify(passwordEncoder).matches(PASSWORD, PASSWORD_HASH);
        }

    }

}
