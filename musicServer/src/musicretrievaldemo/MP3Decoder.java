/*
 * TestMp3.java
 *
 * Created on August 3, 2004, 9:33 PM
 */

package musicretrievaldemo;

import javazoom.jl.decoder.*;
import javax.sound.sampled.AudioFormat;
import java.io.*;

/**
 *
 * @author  yke Yan Ke
 */
public class MP3Decoder{

  
    /** Creates a new instance of TestMp3 */
    public static byte [] decode(File f, float startms, float stopms,
        AudioFormat [] audioFormat) {
            
        //System.out.println("Start ms: " + startms + "   Stopms: " + stopms);
        
        Decoder decoder = new Decoder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitstream bitstream = null;

        boolean setFormat = false;
        
        try {
            bitstream = new Bitstream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return baos.toByteArray();
        }
        
        float readms = 0;
        
        try {
            while (readms < startms) {
                Header h = bitstream.readFrame();
                if (h == null)
                    break;

                //System.out.println("Skipped: " + readms);
                readms += h.ms_per_frame();
                bitstream.closeFrame();

            }

            //int xt1 = 0, xt2 = 0;
            
            while (readms < stopms) {
                Header h = bitstream.readFrame();
                if (h == null)
                    break;
                
                if (setFormat == false) {
                    setFormat = true;
                    // set audioFormat here
                    boolean mono = h.mode() == Header.SINGLE_CHANNEL;
                    audioFormat[0] = new AudioFormat(h.frequency(), 16, mono ? 1 : 2, true, true);
                    //System.out.println(audioFormat[0]);
                    //System.out.println(h.calculate_framesize() + " "  + h.ms_per_frame());
                }
                
                
                //System.out.println("Read: " + h.framesize + " " + h.ms_per_frame() + " " +  h.frequency() + " " + readms);
                //System.out.println(h.toString());
                
                
                readms += h.ms_per_frame();
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);
                short [] sbuf = output.getBuffer();
                //System.out.println("buf len " + sbuf.length);
/*
                while (xt1 < 2000 && xt2 < sbuf.length) {
                    System.out.println(" id: " + xt1/2 + "  raw1: " + sbuf[xt2] + " " + sbuf[xt2 + 1]);
                    xt1 += 2;
                    xt2 += 2;
                    if (xt2 >= sbuf.length) {
                        xt2 = 0;
                        break;
                    }
                }
                */
                for (int i = 0; i < sbuf.length; i++) {
                    baos.write((byte) ((short) (sbuf[i] >> 8)));
                    baos.write(sbuf[i] & 0xff);
                    //System.out.println(sbuf[i]);
                    //baos.write(sbuf[i] & 0xff);
                    //baos.write((byte) ((short) (sbuf[i] >> 8)));
                    
                }

                bitstream.closeFrame();

            }


            bitstream.close();
        } catch (BitstreamException e) {
            System.out.println(e);
        } catch (DecoderException e) {
            System.out.println(e);
        }

        
        return baos.toByteArray();
    }
   
}
