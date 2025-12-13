@echo off
REM Run the Spring Boot application with JDK 21
set JAVA_HOME=C:\Users\Dragon Emperor\Downloads\jdk21\jdk-21.0.9
call mvn spring-boot:run %*
