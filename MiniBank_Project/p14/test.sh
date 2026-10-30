#!/bin/bash
# Compiles and runs the JUnit 5 tests.   Usage: ./test.sh
set -e
JUNIT=lib/junit-platform-console-standalone.jar

if [ ! -f "$JUNIT" ]; then
  mkdir -p lib
  echo "Downloading JUnit 5 console launcher..."
  curl -L -o "$JUNIT" https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
fi

./build.sh
rm -rf out-test
mkdir out-test
javac -cp "out:$JUNIT" -d out-test test/*.java
java -jar "$JUNIT" execute --class-path "out:out-test" --scan-class-path out-test
