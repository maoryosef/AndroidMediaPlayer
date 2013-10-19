/*
 * MP3Player.java
 *
 * Created on August 3, 2004, 10:38 PM
 */

package musicretrievaldemo;
import java.io.*;
import javax.sound.sampled.*;
/**
 *
 * @author  yke Yan Ke
 */
public class MP3Player extends Thread {
    
    private String strFilename;
    private boolean quitting = false;
    CallbackInterface cbi;
    float starttime;
    float duration;
    
    public void play(String fn) {
        duration = Float.MAX_VALUE;
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

            AudioFormat	[] audioFormatA = new AudioFormat[1];
            
            byte [] data = MP3Decoder.decode(soundFile, starttime * 1000, (starttime + duration) * 1000, audioFormatA);

            AudioFormat audioFormat = audioFormatA[0];
            
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

            
            /*
              Still not enough. The line now can receive data,
              but will not pass them on to the audio output device
              (which means to your sound card). This has to be
              activated.
            */
            line.start();

            int writesize = 102400;
            int totalCount = 0;
            byte [] buf = new byte [writesize];
            
            while (totalCount < data.length) {
                int copylen = Math.min(writesize, data.length - totalCount);
                for (int i = 0; i < copylen; i++)
                    buf[i] = data[totalCount + i];
                
                line.write(buf, 0, copylen);
                cbi.setProgress(100 * totalCount  / data.length);
                totalCount += copylen;
            }
            
            // int	nBytesWritten = line.write(data, 0, data.length);
            

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
            

            strFilename = null;
            System.out.println("Finished playing.");
           
            cbi.setProgress(100);
            cbi.donePlaying();
        }
    }
    
    /** Creates a new instance of MP3Player */
    public MP3Player(CallbackInterface cb) {
        this.cbi = cb;
    }
    
}
