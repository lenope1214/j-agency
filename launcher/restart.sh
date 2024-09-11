# linux/unix 환경에서 사용할 수 있는 재시작 파일
# 이 파일을 실행하면 new_jagency.jar 파일을 기존 파일을 대체하고 서버가 재시작됩니다.

# new_jagency.jar 파일이 존재하지 않으면 종료
if [ ! -f new_jagency.jar ]; then
    echo "new_jagency.jar 파일이 존재하지 않습니다."
    exit 1
fi

# 18032 포트로 실행된 프로세스 종료
lsof -i :18032 | tail -1 | awk '{print $2}' | xargs -I {} kill {}

# new_jagency.jar 파일을 기존 파일로 대체
mv new_jagency.jar jagency.jar

# daegyung.bat 파일 실행
sh start.sh
