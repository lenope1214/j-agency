for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":18032"') do taskkill /PID %%a /F
