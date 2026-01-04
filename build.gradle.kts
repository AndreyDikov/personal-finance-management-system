plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.sf"
version = "0.0.1-SNAPSHOT"
description = "personal-finance-management-system"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //spring boot
    implementation("org.springframework.boot:spring-boot-starter")

    //spring web
    implementation("org.springframework.boot:spring-boot-starter-web")

    //валидация
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
    testCompileOnly("org.jetbrains:annotations:26.0.2-1")

    //работа с PostgresQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    //миграции в БД
    implementation("org.springframework.boot:spring-boot-starter-liquibase")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    //Маппинг объектов между слоями
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    //openapi -- http://localhost:8080/swagger-ui.html
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")

    //security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    //тестирование
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
