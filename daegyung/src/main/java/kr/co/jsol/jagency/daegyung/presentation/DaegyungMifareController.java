package kr.co.jsol.jagency.daegyung.presentation;

import kr.co.jsol.jagency.mifare.application.MifareRestServiceImpl;
import kr.co.jsol.jagency.mifare.application.dto.WriteMifareDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/daegyung/mifare")
public class DaegyungMifareController {

    private final MifareRestServiceImpl mifareTagService;

    public DaegyungMifareController(MifareRestServiceImpl mifareTagService) {
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

    @GetMapping("/write")
    @ResponseStatus(HttpStatus.OK)
    public void write(WriteMifareDto writeMifareDto) {
        try {
            // 대경방사선에선 tagNo만 사용한다.
            mifareTagService.write(writeMifareDto);
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
