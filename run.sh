#!/usr/bin/env bash
# Compila y ejecuta ChatUp sin Maven (requiere JDK 17+).
set -e
cd "$(dirname "$0")"
mkdir -p lib
M=https://repo1.maven.org/maven2
[ -f lib/sqlite-jdbc.jar ] || curl -sSfL -o lib/sqlite-jdbc.jar $M/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar
[ -f lib/slf4j-api.jar ]   || curl -sSfL -o lib/slf4j-api.jar   $M/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
[ -f lib/slf4j-nop.jar ]   || curl -sSfL -o lib/slf4j-nop.jar   $M/org/slf4j/slf4j-nop/1.7.36/slf4j-nop-1.7.36.jar
rm -rf build/classes
javac -encoding UTF-8 -d build/classes -cp "lib/*" $(find src/main/java -name "*.java")
java -cp "build/classes:lib/*" com.chatup.Main
