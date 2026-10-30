@echo off
REM Compiles and runs the JUnit 5 tests.   Usage: test.bat
set JUNIT=lib\junit-platform-console-standalone.jar
if not exist %JUNIT% (
  mkdir lib
  echo Downloading JUnit 5 console launcher...
  curl -L -o %JUNIT% https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
)
call build.bat
if exist out-test rmdir /s /q out-test
mkdir out-test
javac -cp "out;%JUNIT%" -d out-test test\BankServiceTest.java test\RepositoryAndAnnotationTest.java
java -jar %JUNIT% execute --class-path "out;out-test" --scan-class-path out-test
