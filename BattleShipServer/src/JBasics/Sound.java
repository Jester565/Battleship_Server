package JBasics;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	private static final String constantFile = "C:\\Users\\HP\\Desktop\\GameImages\\";
	private Clip clip = null;
	private AudioInputStream ais = null;
	private FloatControl volumeControl;
	float masterVolume = .8f;
	public Sound()
{
	
}
public void setClip(String s)
{
	try {
		ais = AudioSystem.getAudioInputStream(new File(constantFile+s));
	} catch (UnsupportedAudioFileException e) {
		
	} catch (IOException e) {
		
	}
	DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
	try {
		clip = (Clip) AudioSystem.getLine(info);
	} catch (LineUnavailableException e) {
		
	}
}
public void playSound(double scaleOff)
{
	try {
		clip.open(ais);
	} catch (LineUnavailableException e) {
		
	} catch (IOException e) {
		
	}
	volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	clip.start();
	volumeControl.setValue((float) ((scaleOff+masterVolume)*(Math.abs(volumeControl.getMaximum()) + Math.abs(volumeControl.getMinimum()))-Math.abs(volumeControl.getMinimum())));
}
public void pauseSound()
{
	clip.stop();
}
public void resumeSound()
{
	clip.start();
}
public void drainClose()
{
	clip.stop();
	clip.drain();
	clip.close();
}
public void setVolume(double scale)
{
	volumeControl.setValue((float) (scale*(Math.abs(volumeControl.getMaximum()) + Math.abs(volumeControl.getMinimum()))-Math.abs(volumeControl.getMinimum())));
}
}