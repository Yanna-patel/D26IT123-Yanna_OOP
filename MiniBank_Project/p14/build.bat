@echo off
REM Compiles MiniBank and builds the runnable JAR.   Usage: build.bat
if exist out rmdir /s /q out
mkdir out
dir /s /b *.java | findstr /v /i "\\test\\" > sources.txt
javac -d out @sources.txt
del sources.txt
jar cfm MiniBank.jar manifest.txt -C out .
echo Built MiniBank.jar  -  run it with:  java -jar MiniBank.jar
