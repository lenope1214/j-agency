package kr.co.jsol.jagency.reader.application.runner;

import org.springframework.boot.CommandLineRunner;

public interface TagRunner extends CommandLineRunner {
    String ip = "";
    String apiUri = "";

    // 서버에서 데이터를 받는다던가 초기 데이터 세팅을 한다던가 한다.
    void init();
}
