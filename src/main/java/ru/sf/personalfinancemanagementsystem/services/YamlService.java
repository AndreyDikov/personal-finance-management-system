package ru.sf.personalfinancemanagementsystem.services;

public interface YamlService {

    String getSecret();
    String getIssuer();
    long getTtlSeconds();

}
