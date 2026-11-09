@echo off
REM Compila y ejecuta ChatUp sin Maven (requiere JDK 17+).
cd /d "%~dp0"
if not exist lib mkdir lib
set M=https://repo1.maven.org/maven2
if not exist lib\sqlite-jdbc.jar curl -sSfL -o lib\sqlite-jdbc.jar %M%/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar
if not exist lib\slf4j-api.jar   curl -sSfL -o lib\slf4j-api.jar   %M%/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
if not exist lib\slf4j-nop.jar   curl -sSfL -o lib\slf4j-nop.jar   %M%/org/slf4j/slf4j-nop/1.7.36/slf4j-nop-1.7.36.jar
if exist build\classes rmdir /s /q build\classes
dir /s /b src\main\java\*.java > fuentes.txt
javac -encoding UTF-8 -d build\classes -cp "lib/*" @fuentes.txt
del fuentes.txt
java -cp "build\classes;lib/*" com.chatup.Main
