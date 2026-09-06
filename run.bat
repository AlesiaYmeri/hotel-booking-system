@echo off
REM Compiles and runs the Hotel Booking System.
REM Requires a JDK (Java 11+) installed and on your PATH.
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 goto :eof
java -cp out com.hotel.Server
