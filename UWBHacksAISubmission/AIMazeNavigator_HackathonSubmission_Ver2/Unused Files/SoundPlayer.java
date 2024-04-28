import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {

    public static void playSound(String soundFileName) {
        try {
            // Open an audio input stream.
            URL url = SoundPlayer.class.getResource(soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        }
    }

    public static void gooseHonk() {
        playSound("honk-sound.wav");
    }

    /*
    public static void main(String[] args) {
        gooseHonk();
    }
    */
}
