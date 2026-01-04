package ru.sf.personalfinancemanagementsystem.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    @Column(name = "login", nullable = false, length = 128)
    String login;

    @Column(name = "password_hash", nullable = false, length = 100)
    String passwordHash;

}
