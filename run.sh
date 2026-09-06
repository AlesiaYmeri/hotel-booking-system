#!/bin/bash
# Compiles and runs the Hotel Booking System.
# Requires a JDK (Java 11+) installed and on your PATH.
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.hotel.Server
