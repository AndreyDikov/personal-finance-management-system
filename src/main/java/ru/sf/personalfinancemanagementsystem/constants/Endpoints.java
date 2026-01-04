package ru.sf.personalfinancemanagementsystem.constants;

import lombok.experimental.UtilityClass;


@UtilityClass
public class Endpoints {

    public static final String BASE = "/pfms";

    public static final String AUTHENTICATION = BASE + "/authentication";
    public static final String REGISTER = AUTHENTICATION + "/register";
    public static final String GET_TOKEN = AUTHENTICATION + "/get-token";

    public static final String OPEN_API = "/swagger-ui";
    public static final String OPEN_API_HTML = OPEN_API + ".html";
    public static final String OPEN_API_ALL = OPEN_API + "/**";

}
