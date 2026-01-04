package ru.sf.personalfinancemanagementsystem.services.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sf.personalfinancemanagementsystem.services.YamlService;
import ru.sf.personalfinancemanagementsystem.constants.YamlPaths;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YamlServiceImpl implements YamlService {

    @Value(YamlPaths.SECRET)
    String secret;

    @Value(YamlPaths.TTL_SECONDS)
    long ttlSeconds;

    @Value(YamlPaths.ISSUER)
    String issuer;

}
