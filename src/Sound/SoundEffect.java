package Sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundEffect {
	static Clip clip;
	String fileName;
	static AudioInputStream soundStream;
	public static float decibelValueRaiser;
	
	public static void play(String fileName) {
		File file = new File(fileName);
		try {
			soundStream = AudioSystem.getAudioInputStream(file);
			clip = AudioSystem.getClip();
			clip.open(soundStream);
			FloatControl controlOfSound = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			controlOfSound.setValue(decibelValueRaiser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		clip.start();
	}
}
