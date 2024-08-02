# Reader 모듈
 - RFID 태그 기기가 다양하고 각 태그 기기마다 지원하는 방식이 다르기 때문에,  
    공통적으로 사용할 수 있도록 인터페이스를 제공하여 개발에 틀을 잡아줄 수 있도록 함.  
- 현재 acr122, ccr-hfn 모듈에서 include 하여 사용하고 있음

## TagRunner
- 태깅시 사용될 기본 정보를 관리하는 CommandLineRunner 인터페이스
- CommandLineRunner로 구현한 이유는, 서버와 별개로 구동시 동시에 실행되도록 하기 위함

## Readable
- 태그 리드 인터페이스로 필요한 정보를 토대로 run 메소드를 구현한다.
