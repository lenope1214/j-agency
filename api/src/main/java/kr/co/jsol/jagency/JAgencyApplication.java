package kr.co.jsol.jagency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

//// 비동기 실행을 위해 비동기 설정.
@EnableAsync
@SpringBootApplication(
        // datasource 설정이 없는 경우 해당 설정을 사용하면 정상 기동됨
        exclude = {DataSourceAutoConfiguration.class}
)
public class JAgencyApplication {

    public static void main(String[] args) {
        // timezone 설정 1
        System.setProperty("user.timezone", "Asia/Seoul");
        SpringApplication.run(JAgencyApplication.class, args);
    }

    // timezone 설정 2
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
