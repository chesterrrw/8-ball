import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class Sounds {

    private final String[] audioClips = {"clack", "bgmusic" };
    public String currentlyPlaying = "";
    private HashMap<String, Clip> clips = new HashMap<String, Clip>();

    private Clip[] clackPool = new Clip[8]; // or more if needed
    private Clip thump;
    private static int index = 0;
    public Sounds() {
            for (int i = 0; i < 8; i++) {
                try {
                    clackPool[i] = AudioSystem.getClip();
                    File file = new File("sounds/clack.wav");
                    AudioInputStream audio2 = AudioSystem.getAudioInputStream(file);
                    clackPool[i].open(audio2);
                } catch (Exception e) {
                    System.out.println("Failed to initialize clackPool at index " + i);
                    e.printStackTrace();
                }
            }
            try {
                thump = AudioSystem.getClip();
                File file = new File("sounds/thump.wav");
                AudioInputStream audio = AudioSystem.getAudioInputStream(file);
                thump.open(audio);
            }
            catch(Exception e){
                System.out.println("Error reading thump");
            }
        //FloatControl volume = (FloatControl) clips.get("explosion").getControl(FloatControl.Type.MASTER_GAIN);
        //volume.setValue(6.0f);
    }

    public void playClack(double relativeV){
        float v = (float) Math.min(relativeV / 1000f, 1.0f);
        float loudness = (float) Math.max(Math.pow(v, 1.3), 0.0001f);

        Clip clip = clackPool[index];
        if (clip == null) System.out.println("null");
        if (clackPool[index] == null) System.out.println("this is null");
        index = (index + 1) % 8;

        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = 20.0f * (float)Math.log10(loudness);//power is proportional to amplitude squared. using log rules,
        // pull the "squared" out to the front, and it becomes 10*2 or 20
        dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
        gain.setValue(dB);
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
        System.out.println((float) (relativeV/2500));
    }
    public void playThump(double percent){
        float v = (float) Math.min(percent, 1.0f);
        float loudness = (float) Math.max(Math.pow(v, 1.3), 0.0001f);
        FloatControl gain = (FloatControl) thump.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = 20.0f * (float)Math.log10(loudness);
        gain.setValue(dB);
        thump.setFramePosition(0);
        thump.start();
    }
}
