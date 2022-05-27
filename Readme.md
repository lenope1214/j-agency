# Jclient 메뉴얼
> 해당 메뉴얼은 클라이언트 PC에 설치되는 Jclient 어플리케이션의 메뉴얼입니다.

### 💻 개발 사양
|     도구     |              버전               |
| :----------: | :-----------------------------: |
|    Spring    |    Spring Boot 2.5.6.RELEASE   |
|      OS      |          Windows 10             |
|   개발 툴    | VSCODE |
|     JDK      |             `JDK 8`             |
|   빌드     |          gradle-7.3.2          |
|   패키지    |          JAR          |
---

## ✅ 프로젝트 실행방법
### 💻  jclient 
- 실행 조건
> open `JDK 8 32bit` 설치 필요
- 실행 스크립트

```shell
$ java -jar jclient.jar --server.port=6114 (--mode=rfid) --ip=192.168.86.23 --target=https://board031.knuh.kr
```

- 파라미터
    - `server.port` : 실행포트
        - 기본 : `6114`
    - `mode` : 실행모드
        - rfid : `rfid`
	- `ip` : 실행 PC 로컬 ip
	- `target` : 메인서버 주소
	- `batUrl` : 배치파일 url
---

- 어플리케이션 기능

1. rfid 태그 시 서버측 태그 api로 환자 이름 및 pid 전송 
2. 클라이언트 브라우저에서 shutdown 플래그 송신 
3. 크롬 닫기 및 실행 bat파일 실행
4. target으로부터 jclient.jar C:/Jsolution/jclient.jar 다운로드

---
- apidoc
    - `/poweroff` : 컴퓨터를 즉시 종료한다
    - `/reboot` : 컴퓨터를 즉시 재부팅한다
	- `/cmd/{command}` : cmd 커맨드를 실행한다 
	- `/chrome/reboot` : 크롬을 종료한 후 batUrl에 작성된 bat file을 실행한다
         - 기본 : chrome.exe 실행
	- `/` : 
---