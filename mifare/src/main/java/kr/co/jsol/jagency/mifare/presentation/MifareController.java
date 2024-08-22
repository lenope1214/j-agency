package kr.co.jsol.jagency.mifare.presentation;

import kr.co.jsol.jagency.mifare.application.MifareRestServiceImpl;
import kr.co.jsol.jagency.mifare.application.dto.WriteMifareDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reader/mifare")
public class MifareController {

    private final MifareRestServiceImpl mifareTagService;

    public MifareController(MifareRestServiceImpl mifareTagService) {
        this.mifareTagService = mifareTagService;
    }

    @GetMapping("/write-test/{value}")
    @ResponseStatus(HttpStatus.OK)
    public void writeTest(@PathVariable String value) {
        try {
            mifareTagService.write(
                    new WriteMifareDto() {{
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
            mifareTagService.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
