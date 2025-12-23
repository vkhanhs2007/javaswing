@echo off
REM Batch file to compile and run Cafe Management System

echo.
echo ============================================
echo   CAFE MANAGEMENT SYSTEM - COMPILE & RUN
echo ============================================
echo.

REM Check if lib folder exists
if not exist "lib" (
    echo ERROR: lib folder not found!
    echo Please download SQL Server JDBC Driver and put it in lib/ folder
    echo Download: https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
    pause
    exit /b 1
)

REM Check if bin folder exists, if not create it
if not exist "bin" (
    echo Creating bin folder...
    mkdir bin
)

REM Compile Java files
echo Compiling Java files...
javac -cp "lib/*" -d bin src\com\cafe\config\*.java 2>nul
if errorlevel 1 (
    echo ERROR: Failed to compile config files
    pause
    exit /b 1
)

javac -cp "lib/*" -d bin src\com\cafe\model\*.java 2>nul
if errorlevel 1 (
    echo ERROR: Failed to compile model files
    pause
    exit /b 1
)

javac -cp "lib/*" -d bin src\com\cafe\dao\*.java 2>nul
if errorlevel 1 (
    echo ERROR: Failed to compile dao files
    pause
    exit /b 1
)

javac -cp "lib/*" -d bin src\com\cafe\util\*.java 2>nul
if errorlevel 1 (
    echo ERROR: Failed to compile util files
    pause
    exit /b 1
)

javac -cp "lib/*" -d bin src\com\cafe\ui\*.java 2>nul
if errorlevel 1 (
    echo ERROR: Failed to compile ui files
    pause
    exit /b 1
)

echo.
echo Compilation completed successfully!
echo.
echo ============================================
echo   Running Cafe Management System...
echo ============================================
echo.

REM Run the application
java -cp "bin;lib/*" com.cafe.ui.LoginFrame

pause
