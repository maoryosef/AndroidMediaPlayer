/*
 * AudioRecorder.java
 *
 * Created on July 6, 2004
 */

/**
 *
 * @author  Yan Ke
 */

package musicretrievaldemo;

import javax.sound.sampled.*;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class AudioRecorder extends Thread {
  
    // 44.1KHz, 16 bits, mono
    //float sampleRate = 44100;
    float sampleRate = 11025;
    
    int sampleSizeInBits = 16;
    int channels = 1;
    int reclen = 0;
    
    int bufferratio = (int) (sampleRate / 11025);
    
    CallbackInterface cbi;
    
    boolean quitting = false;
    
    ByteArrayOutputStream osOut;
    AudioFormat format;
    TargetDataLine line;
    int totalCount = 0;
    
    private String outname;

    private static AudioFileFormat.Type getTargetType(String extension){
        AudioFileFormat.Type[] typesSupported = AudioSystem.getAudioFileTypes();
        //System.out.println("length: " + ypesSupported.length);
        for(int i = 0; i < typesSupported.length; i++){
            if(typesSupported[i].getExtension().equals(extension)) {
                return typesSupported[i];
            } //end if 
        }//end for loop 
  
        return null; //no match  
    }//end getTargetType
    
    
    /** Creates a new instance of AudioRecorder */
    public AudioRecorder(CallbackInterface cbi, String outname) {
        this.outname = outname;
        this.cbi = cbi;
        
        boolean signed = true;
        boolean bigEndian = true;
        format =  new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        
        //System.out.println("IsBigEndian(): " + format.isBigEndian());
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
        
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException e) {
            System.out.println(e);
        
        }
        
        setPriority(MAX_PRIORITY);
    }
    
    void writeToFile() throws IOException {
       
        long lLengthInFrames = totalCount / format.getFrameSize();
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream ios = new PipedInputStream(pos);
        AudioInputStream ais = new AudioInputStream(ios, format, lLengthInFrames);
        WriterThread thread = new WriterThread(ais, "WriteThread", outname);
        thread.start();
        
        osOut.writeTo(pos);
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        
    }
    
  
    private int level(byte [] buffer) {
        float maxval = 0;
        
        for (int i = 0; i < buffer.length; i += 2) {
            int j = (buffer[i] << 8) | (buffer[i+1] & 0xff);
            float val = (float) j / (float) Short.MAX_VALUE;
            maxval = Math.max(maxval, Math.abs(val));
            //System.out.println(buffer[i]*256 + buffer[i+1]);
        }
        
        // min level is -5, max is 0
        // normalize to 0 t 100
        double levd = Math.log(maxval) * 20;
        
        //return (int) (maxval * levelMultiplier);
        int lev = (int) levd + 100;
        //System.out.println("Maxval: " + maxval + "   Lev: " + lev);
        return lev;
        
    }
    public void run() {
 
        while (!quitting) {
            while (reclen == 0 ) {
                try {
                    sleep(10); //ms
                } catch (InterruptedException e) {}
            }
            
            List buffers = new LinkedList();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            line.start();

            int bufferSize = (int)format.getSampleRate() * format.getFrameSize() / bufferratio;
            byte buffer[] = new byte[bufferSize];

            boolean externalTrigger = true;

            totalCount = 0;
            System.out.println("Buffersize: " + bufferSize);

            osOut = new ByteArrayOutputStream();

            System.out.println("Starting new recording.");
            
            // we read some extra bytes to flush the buffer - bug in Java sound
            line.read(buffer, 0, Math.min(3000, buffer.length));
            
            while (totalCount < bufferSize * bufferratio * reclen) {
                cbi.setProgress(100 * totalCount / (bufferSize * bufferratio * reclen));
                
                int count = line.read(buffer, 0, buffer.length);
                
                cbi.setLevel(level(buffer));
                
                osOut.write(buffer, 0, count);
                baos.write(buffer, 0, count);
                //System.out.println("Size of bytearrayoutputstream: " + baos.size());
                
                totalCount += count;
            }

            cbi.setProgress(100);
            
            line.stop();
            
            reclen = 0;
            cbi.setLevel(0);
            cbi.doneRecording(baos.toByteArray(), (int) sampleRate);
            
            System.out.println("Clip finished.  Writing to disk...");
            try {
                writeToFile();
                osOut.close();
            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }
    
    public void record(int reclen) {
        this.reclen = reclen;
    }
        
    public class WriterThread extends Thread {
        AudioInputStream ais;
        String fn;
        
        public WriterThread(AudioInputStream ais, String str, String fn) {
            super(str);
            this.ais = ais;
            this.fn = fn;
            //System.out.println("Starting new thread " + str);
            setPriority(MIN_PRIORITY);
        }
        
        public void run() {
            File outputFile = new File(fn);
            int nWrittenBytes = 0;

            try {
                nWrittenBytes = AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println("Wrote " + nWrittenBytes + " bytes to file " + fn);
        }
    }
}
