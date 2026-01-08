package ru.sf.personalfinancemanagementsystem.services.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.sf.personalfinancemanagementsystem.services.YamlService;


@Getter
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YamlServiceImpl implements YamlService {

    @Value("${spring.security.jwt.secret}")
    String secret;

    @Value("${spring.security.jwt.ttl-seconds}")
    long ttlSeconds;

    @Value("${spring.security.jwt.issuer}")
    String issuer;

    @Value("${spring.security.jwt.algorithm-name}")
    String algorithmName;

    @Value("${spring.security.jwt.min-byte-size}")
    int minByteSize;

}
