package ru.sf.personalfinancemanagementsystem.domains;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {

    String token;
    Instant expiresAt;

}
