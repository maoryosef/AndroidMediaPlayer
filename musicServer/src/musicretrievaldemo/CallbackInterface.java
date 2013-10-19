/*
 * CallbackInterface.java
 *
 * Created on July 17, 2004, 7:02 PM
 */

package musicretrievaldemo;

import java.util.List;

/**
 *
 * @author  yke Yan Ke
 */
public interface CallbackInterface {
    public void doneRecording(byte[] buffer, int freq);
    public void donePlaying();
    public void setProgress(int progress); // from 0 to 100
    public void setLevel(int level); // from 0 to 100
}
