/*
 * AudioPlayer.java
 *
 * Created on July 17, 2004, 5:50 PM
 */

package musicretrievaldemo;

import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author  yke Yan Ke
 */
public class AudioPlayer extends Thread{
    
    private String strFilename;
    private boolean quitting = false;
    CallbackInterface cbi;
    float starttime;
    float duration;
    
    public void play(String fn) {
        duration = 0;
        starttime = 0;
        strFilename = fn;
    }
    
    public void play(String fn, float start, float dur) {
        starttime = start;
        duration = dur;
        strFilename = fn;
        //System.out.println("play(): Playing " + strFilename);
    }
    
    public void quit() {
        quitting = true;
    
    }
    
    public void run() {
        
        while (!quitting) {
            while (strFilename == null) {
                try {
                    sleep(10); //ms
                } catch (InterruptedException e) {}
            }

            System.out.println("Playing " + strFilename);
            
            File	soundFile = new File(strFilename);

            /*
              We have to read in the sound file.
            */
            AudioInputStream	audioInputStream = null;
            try
            {
                    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            }
            catch (Exception e)
            {
                    /*
                      In case of an exception, we dump the exception
                      including the stack trace to the console output.
                      Then, we exit the program.
                    */
                    e.printStackTrace();
                    System.exit(1);
            }

            /*
              From the AudioInputStream, i.e. from the sound file,
              we fetch information about the format of the
              audio data.
              These information include the sampling frequency,
              the number of
              channels and the size of the samples.
              These information
              are needed to ask Java Sound for a suitable output line
              for this audio file.
            */
            AudioFormat	audioFormat = audioInputStream.getFormat();
            int bufferSize = (int)audioFormat.getSampleRate() * audioFormat.getFrameSize() / 4;
            
                    System.out.println("Output buffer size: " + bufferSize);
            /*
              Asking for a line is a rather tricky thing.
              We have to construct an Info object that specifies
              the desired properties for the line.
              First, we have to say which kind of line we want. The
              possibilities are: SourceDataLine (for playback), Clip
              (for repeated playback)	and TargetDataLine (for
              recording).
              Here, we want to do normal playback, so we ask for
              a SourceDataLine.
              Then, we have to pass an AudioFormat object, so that
              the Line knows which format the data passed to it
              will have.
              Furthermore, we can give Java Sound a hint about how
              big the internal buffer for the line should be. This
              isn't used here, signaling that we
              don't care about the exact size. Java Sound will use
              some default value for the buffer size.
            */
            SourceDataLine	line = null;
            DataLine.Info	info = new DataLine.Info(SourceDataLine.class,
                                                                                             audioFormat);
            try
            {
                    line = (SourceDataLine) AudioSystem.getLine(info);

                    /*
                      The line is there, but it is not yet ready to
                      receive audio data. We have to open the line.
                    */
                    line.open(audioFormat);
            }
            catch (LineUnavailableException e)
            {
                    e.printStackTrace();
                    System.exit(1);
            }
            catch (Exception e)
            {
                    e.printStackTrace();
                    System.exit(1);
            }
    /* default is loudest anyway
     *
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            double gain = 1D; // 0 - 1
            float dB = (float)(Math.log(gain)/Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
     **/
            /*
              Still not enough. The line now can receive data,
              but will not pass them on to the audio output device
              (which means to your sound card). This has to be
              activated.
            */
            line.start();

            /*
              Ok, finally the line is prepared. Now comes the real
              job: we have to write data to the line. We do this
              in a loop. First, we read data from the
              AudioInputStream to a buffer. Then, we write from
              this buffer to the Line. This is done until the end
              of the file is reached, which is detected by a
              return value of -1 from the read method of the
              AudioInputStream.
            */
            
            long nframes = audioInputStream.getFrameLength();
            long nbytes = nframes * audioFormat.getFrameSize();
            
            //System.out.println("nFrames " + nframes + "   nBytes " + nbytes);
            
            int bps = (int)audioFormat.getSampleRate() * audioFormat.getFrameSize();
            
            int startbytes = (int) (bps * starttime);
            int durationbytes = (int) (bps * duration);
            
            //System.out.println("Bytes per second: " + bps + "  Start bytes: "
            //    + startbytes + "  Duration bytes: " + durationbytes);
            
            int	nBytesRead = 0;
            int count = 0;
            byte[]	abData = new byte[bufferSize];
            while (nBytesRead != -1)
            {
                    try
                    {
                            nBytesRead = audioInputStream.read(abData, 0, abData.length);
                    }
                    catch (IOException e)
                    {
                            e.printStackTrace();
                    }
                    
                    if (nBytesRead >= 0)
                        count += nBytesRead;
                    
                    if (nBytesRead >= 0 && count >= startbytes)
                    {
                            int	nBytesWritten = line.write(abData, 0, nBytesRead);
                            
                            if (durationbytes > 0)
                                cbi.setProgress(100 * (count - startbytes) / durationbytes);
                            else
                                cbi.setProgress((int) (100 * count / nbytes));
                    }
                    
                    if (durationbytes > 0 && count >= startbytes + durationbytes)
                        break;
            }

            /*
              Wait until all data are played.
              This is only necessary because of the bug noted below.
              (If we do not wait, we would interrupt the playback by
              prematurely closing the line and exiting the VM.)

              Thanks to Margie Fitch for bringing me on the right
              path to this solution.
            */
            line.drain();

            /*
              All data are played. We can close the shop.
            */
            line.close();
            try {
                audioInputStream.close();
            } catch (IOException e) {
                System.out.println(e);
            }

            strFilename = null;
            System.out.println("Finished playing.");
           
            cbi.setProgress(100);
            cbi.donePlaying();
        }
    }

    public AudioPlayer(CallbackInterface cbi) {
        this.cbi = cbi;
        setPriority(MAX_PRIORITY);
        //System.out.println("AudioPlayer Priority: " + getPriority());
    }

   
}
