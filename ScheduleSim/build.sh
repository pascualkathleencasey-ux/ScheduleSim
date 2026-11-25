#!/bin/sh
# Builds the executable JAR. Needs only a JDK (Java 8 or newer).
set -e
rm -rf out && mkdir -p out
javac -d out src/Main.java src/cpu/*.java src/disk/*.java src/gui/*.java
printf 'Main-Class: Main\n' > out/manifest.txt
jar cfm ScheduleSim.jar out/manifest.txt -C out .
echo "Built ScheduleSim.jar  (run with: java -jar ScheduleSim.jar)"
