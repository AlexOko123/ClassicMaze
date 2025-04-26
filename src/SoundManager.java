import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SoundManager {

    private static List<String> musicTracks = new ArrayList<>();
    private static int currentTrackIndex = 2;
    private static Clip currentMusicClip = null;

    public static void addMusicTrack(String soundFilePath) {
        musicTracks.add(soundFilePath);
    }

    public static void startMusicPlaylist() {
        if (musicTracks.isEmpty()) return;
        playMusicTrack(musicTracks.get(currentTrackIndex));
    }

    private static void playMusicTrack(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            // When track ends, automatically play next
            currentMusicClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    currentMusicClip.close();
                    currentTrackIndex = (currentTrackIndex + 1) % musicTracks.size();
                    playMusicTrack(musicTracks.get(currentTrackIndex));
                }
            });

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
            currentMusicClip.close();
        }
    }

    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
