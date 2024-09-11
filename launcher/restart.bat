echo 3초 뒤에 서버가 재시작됩니다.
timeout /t 3 /nobreak

echo 기존에 실행중인 서버를 종료합니다.
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":18032"') do taskkill /PID %%a /F

if not exist new_jagency.jar (
    echo new_jagency.jar file not exists
    exit /b
) else (
    copy /Y new_jagency.jar jagency.jar
)

echo 서버를 재시작합니다.
start /b start.bat

