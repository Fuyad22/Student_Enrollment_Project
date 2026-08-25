@echo off
echo ==================================================
echo Building Timetable ^& Enrollment Portal Windows App
echo ==================================================

echo.
echo [1/6] Cleaning up old builds...
if exist bin rmdir /s /q bin
if exist dist rmdir /s /q dist
if exist build-dist rmdir /s /q build-dist

echo.
echo [2/6] Compiling Java classes...
mkdir bin
javac -cp "lib/*" -d bin src\dbms\*.java
if %ERRORLEVEL% neq 0 (
    echo Error: Compilation failed!
    exit /b %ERRORLEVEL%
)

echo.
echo [3/6] Packaging classes into JAR...
mkdir dist\lib
copy lib\sqlite-jdbc-3.42.0.0.jar dist\lib\ > nul
jar -cfm dist\TimetablePortal.jar manifest.txt -C bin dbms
if %ERRORLEVEL% neq 0 (
    echo Error: JAR creation failed!
    exit /b %ERRORLEVEL%
)

echo.
echo [4/6] Generating standalone Windows app-image...
jpackage --name "TimetablePortal" --input dist --main-jar TimetablePortal.jar --main-class dbms.MainDashboard --type app-image --dest build-dist
if %ERRORLEVEL% neq 0 (
    echo Error: jpackage failed!
    exit /b %ERRORLEVEL%
)

echo.
echo [5/6] Copying database file to application directory...
copy timetable.db build-dist\TimetablePortal\ > nul
if %ERRORLEVEL% neq 0 (
    echo Warning: Could not copy timetable.db to build-dist\TimetablePortal\.
)

echo.
echo [6/6] Build complete!
echo Standalone application is available at: build-dist\TimetablePortal\TimetablePortal.exe
echo ==================================================
