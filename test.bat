@echo off
REM Run the tests with JDK 21
set JAVA_HOME=C:\Users\Dragon Emperor\Downloads\jdk21\jdk-21.0.9
call mvn clean test %*
