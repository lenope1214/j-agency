package knuh.rfid.util;


import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;
import javax.swing.*;

import jaco.mp3.player.MP3Player;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.time.LocalDateTime;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class JavaBeepTest {

    public File convertInputStreamToFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile(String.valueOf(inputStream.hashCode()), ".tmp");
        // jvm 종료 시 같이 지워지도록
        tempFile.deleteOnExit();

        copyInputStreamToFile(inputStream, tempFile);

        return tempFile;
    }

//    @Test
//    public void 자바비프음테스트() {
////        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        for (int i = 0; i < 5; i++) {
////            toolkit.beep();
//            try {
////                playSound("/Users/iseongbog/workspace/Github/JClient/src/test/resources/beep.mp3");
////                System.out.println("삡");
////
////                Thread.sleep(1000);
//
////                InputStream inputStream = new ClassPathResource("/Users/iseongbog/workspace/Github/JClient/src/test/resources/beep.mp3").getInputStream();
////                InputStream inputStream = new InputStream("beep.mp3");
////                File file = convertInputStreamToFile(inputStream);
//                File file = new File("/Users/iseongbog/workspace/Github/JClient/src/main/resources/beep.mp3");
//                MP3Player mp3Player = new MP3Player(file);
//                mp3Player.play();
//
//                while (!mp3Player.isStopped()) {
//                    Thread.sleep(100);
//                }
//            } catch (Exception e) {
//                System.out.println("e = " + e);
//            }
//        }
//    }

//    @Test
//    public void JACO사운드테스트() {
//
//    }

    public void playSound(String fileName) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        try {
            System.out.println("fileName = " + fileName);
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
            System.out.println("ais = " + ais);
            Clip clip = AudioSystem.getClip();
            System.out.println("clip = " + clip);
            int frameLength = clip.getFrameLength();
            System.out.println("frameLength = " + frameLength);
            clip.stop();
            clip.open(ais);
            clip.start();
            clip.close();
            ais.close();
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void 자바비프음시간테스트(){
        try {
            System.out.println("beep start time : " + LocalDateTime.now());
            InputStream inputStream = new ClassPathResource("beep.mp3").getInputStream();
            File file = convertInputStreamToFile(inputStream);
            MP3Player mp3Player = new MP3Player(file);
            mp3Player.play();

            // 아래 실행 확인이 없으면 소리가 나지 않는다.
            // 필수 처리############################################
            while (!mp3Player.isStopped()) {
                Thread.sleep(10);
            }
            // 필수 처리############################################
            System.out.println("beep end time : " + LocalDateTime.now());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        assert (true);
    }

}
