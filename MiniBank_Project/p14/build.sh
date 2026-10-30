#!/bin/bash
# Compiles MiniBank and builds the runnable JAR.   Usage: ./build.sh
set -e
rm -rf out
mkdir out
javac -d out $(find . -name '*.java' -not -path './test/*')
jar cfm MiniBank.jar manifest.txt -C out .
echo "Built MiniBank.jar  ->  run it with:  java -jar MiniBank.jar"
