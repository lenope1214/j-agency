# Jclient 메뉴얼
> 해당 메뉴얼은 클라이언트 PC에 설치되는 Jclient 어플리케이션의 메뉴얼입니다.

### 💻 개발 사양
|     도구     |              버전               |
| :----------: | :-----------------------------: |
|    Spring    |    Spring Boot 2.5.6.RELEASE   |
|      OS      |          Windows 10             |
|   개발 툴    | VSCODE |
|     JDK[rfid]      |             [`JDK 8 32bit`]
|     JDK[default]      |           [`JDK 1.8↑ any bit`]             |
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
	- `/jclient/download` : target 서버에서 jclient 파일을 받아온다.
---

RFID 모드로 실행했을 때, %1은(는) 올바른 Win32 응용 프로그램이 아닙니다 오류 발생 시
32bit jdk로 지정 후 실행한다.

---


### 대경방사 RFID (ACR122)

1. Mifare 1K 태그를 사용함.
2. ACR122U 리더기를 사용함.

### Mifare 1K 태그

- Mifare 1K 태그는 16개의 섹터로 구성되어 있음.
- 각 섹터는 4개의 블록으로 구성되어 있음.
- 각 블록은 16바이트로 구성되어 있음.
- 전체 블록은 총 64개의 블록으로 구성되어 있음. 0~63
- 블록 0은 Manufacture Blcok 이라고 하며 카드 제조사 코드 및 카드 시리얼 넘버등이 기록되어 있습니다. 태그에 따라 수정이 가능은 하지만 수정을 권장하지 않음.
- 각 섹터의 0~2 블록은 읽기/쓰기가 가능하지만 각 섹터의 마지막(3) 블록은 식별에 사용하기 때문에 수정하면 데드블록이 될 수 있음.
- 
