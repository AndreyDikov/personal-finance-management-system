package ru.sf.personalfinancemanagementsystem.constants;

import lombok.experimental.UtilityClass;


@UtilityClass
public class Endpoints {

    public static final String BASE = "/pfms";

    public static final String ERROR = "/error";

    public static final String AUTHENTICATION = BASE + "/authentication";
    public static final String REGISTER = AUTHENTICATION + "/register";
    public static final String GET_TOKEN = AUTHENTICATION + "/get-token";

    public static final String SWAGGER_UI = "/swagger-ui";
    public static final String SWAGGER_UI_HTML = SWAGGER_UI + ".html";
    public static final String SWAGGER_UI_ALL = SWAGGER_UI + "/**";

    public static final String CATEGORIES = BASE + "/categories";
    public static final String CREATE_CATEGORY = CATEGORIES + "/create";
    public static final String SET_BUDGET_AMOUNT = CATEGORIES + "/set-budget-amount";
    public static final String VIEW_CATEGORIES_REPORT = CATEGORIES + "/view-categories-report";

    public static final String OPERATIONS = BASE + "/operations";
    public static final String CREATE_OPERATION = OPERATIONS + "/create";
    public static final String VIEW_GENERAL_REPORT = OPERATIONS + "/view-general-report";

}
