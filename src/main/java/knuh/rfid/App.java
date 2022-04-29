package knuh.rfid;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication

//https://www.hanumoka.net/2020/07/02/springBoot-20200702-sringboot-async-service/
// 특정 exe파일을 실행하거나 해서 스프링프로젝트가 대기(동기)하는 상황에서
// 비동기 실행을 위해 비동기 설정.
@EnableAsync
public class App {


	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
