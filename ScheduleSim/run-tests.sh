#!/bin/sh
# Compiles the algorithms with the test file and runs it (expected vs actual).
set -e
rm -rf out-test && mkdir -p out-test
javac -d out-test src/cpu/*.java src/disk/*.java test/AlgoTest.java
java -cp out-test AlgoTest
rm -rf out-test
