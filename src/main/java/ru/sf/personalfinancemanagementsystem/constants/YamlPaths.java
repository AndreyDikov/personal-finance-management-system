package ru.sf.personalfinancemanagementsystem.constants;

import lombok.experimental.UtilityClass;


@UtilityClass
public class YamlPaths {

    public static final String JWT = "${spring.security.jwt";

    public static final String SECRET = JWT + ".secret}";
    public static final String TTL_SECONDS = JWT + ".ttl-seconds}";
    public static final String ISSUER = JWT + ".issuer}";

}
