/*
 * FFTThread.java
 *
 * Created on August 3, 2004, 4:48 PM
 */

package musicretrievaldemo.fft;

/**
 *
 * @author  dwhoiem Derek Hoiem
 */
public class FFTThread extends Thread{
    
    FFTDisplayPanel display;
    double[] data;
    int fs;
    boolean isRecorded;
    
    /** Creates a new instance of FFTThread */
    public FFTThread(FFTDisplayPanel display, double[] data, int fs, boolean isRecorded) {
        this.display = display;
        this.data = data;
        this.fs = fs;
        this.isRecorded = isRecorded;
        setPriority(MIN_PRIORITY);
        //System.out.println("Priority " + getPriority());
        
    }
    
    public void run() {
        display.displayFFT(data, fs, isRecorded);
    }
    
}
