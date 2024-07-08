import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class Sound {
    private Clip clip;

    public Sound(String filePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.err.println("Error loading sound: " + filePath);
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0); // rewind to the beginning
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.setFramePosition(0); // rewind to the beginning
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
