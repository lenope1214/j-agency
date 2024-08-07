package kr.co.jsol.jagency.acr122.presentation;

import kr.co.jsol.jagency.acr122.application.dto.WriteAcr122Dto;
import kr.co.jsol.jagency.acr122.infrastructure.Acr122Repository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/acr122")
public class Acr122Controller {

    private final Acr122Repository acr122Repository;

    public Acr122Controller(Acr122Repository acr122Repository) {
        this.acr122Repository = acr122Repository;
    }

    @GetMapping("/write-test/{value}")
    @ResponseStatus(HttpStatus.OK)
    public void writeTest(@PathVariable String value) {
        try {
            acr122Repository.writeToCards(
                    new WriteAcr122Dto() {{
                        setData(value);
                    }}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/read")
    @ResponseStatus(HttpStatus.OK)
    public void read() {
        try {
            acr122Repository.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
