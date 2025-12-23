#!/bin/bash

# Shell script to compile and run Cafe Management System on Linux/Mac

echo ""
echo "============================================"
echo "  CAFE MANAGEMENT SYSTEM - COMPILE & RUN"
echo "============================================"
echo ""

# Check if lib folder exists
if [ ! -d "lib" ]; then
    echo "ERROR: lib folder not found!"
    echo "Please download SQL Server JDBC Driver and put it in lib/ folder"
    echo "Download: https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server"
    exit 1
fi

# Check if bin folder exists, if not create it
if [ ! -d "bin" ]; then
    echo "Creating bin folder..."
    mkdir bin
fi

# Compile Java files
echo "Compiling Java files..."

javac -cp "lib/*" -d bin src/com/cafe/config/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile config files"
    exit 1
fi

javac -cp "lib/*" -d bin src/com/cafe/model/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile model files"
    exit 1
fi

javac -cp "lib/*" -d bin src/com/cafe/dao/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile dao files"
    exit 1
fi

javac -cp "lib/*" -d bin src/com/cafe/util/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile util files"
    exit 1
fi

javac -cp "lib/*" -d bin src/com/cafe/ui/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile ui files"
    exit 1
fi

echo ""
echo "Compilation completed successfully!"
echo ""
echo "============================================"
echo "  Running Cafe Management System..."
echo "============================================"
echo ""

# Run the application
java -cp "bin:lib/*" com.cafe.ui.LoginFrame
