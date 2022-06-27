:: 명령문 보이지 않게 설정
@echo off

:: 변수 for안에서 가능하게 설정
setlocal enabledelayedexpansion

:: 변수 초기설정
set params=

:: "delims="을 붙여 스페이스바 후로도 읽어옴
for /f "delims=" %%i In (config.txt) DO 
	:: params에 --값 들어가게 함  mode=??면 --mode=??로
	set params=!params! --%%i

jclient %params%

:: 여러번 실행 방지
exit /b