@echo off
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":18032"') do taskkill /PID %%a /F

if not exist new_jagency.jar (
    echo new_jagency.jar file not exists
    exit /b
) else (
    copy /Y new_jagency.jar jagency.jar
)

start /b start.bat
