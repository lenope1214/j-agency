package knuh.rfid.util;


import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;
import javax.swing.*;
import jaco.mp3.player.MP3Player;

import java.io.File;

public class JavaBeepTest {

    @Test
    public void 자바비프음테스트() {
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (int i = 0; i < 5; i++) {
//            toolkit.beep();
            try {
                playSound("/Users/iseongbog/workspace/Github/JClient/src/test/resources/beep.mp3");
                System.out.println("삡");

                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("e = " + e);
            }
        }
    }

    @Test
    public void JACO사운드테스트(){

    }

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


}
