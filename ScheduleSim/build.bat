@echo off
rem Builds the executable JAR. Needs only a JDK (Java 8 or newer).
if exist out rmdir /s /q out
mkdir out
javac -d out src\Main.java src\cpu\*.java src\disk\*.java src\gui\*.java || exit /b 1
echo Main-Class: Main> out\manifest.txt
jar cfm ScheduleSim.jar out\manifest.txt -C out .
echo Built ScheduleSim.jar  (run with: java -jar ScheduleSim.jar)
