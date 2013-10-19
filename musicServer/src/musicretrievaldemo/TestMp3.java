/*
 * TestMp3.java
 *
 * Created on August 3, 2004, 10:53 PM
 */

package musicretrievaldemo;

/**
 *
 * @author  yke Yan Ke
 */

public class TestMp3 {
    
    /** Creates a new instance of TestMp3 */
    public TestMp3() {
        Test t = Test.getInstance();

        t.
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        /*
        for (short i = java.lang.Short.MIN_VALUE; i < java.lang.Short.MAX_VALUE; i++) {
            
        //for (short i = 0; i < 1000; i++) {
            byte b1 = (byte) ((short) (i >> 8));
            byte b2 = (byte) (i & 0xff);

             
            int j = (b1 << 8) | (b2 & 0xff);
            int k = b1 * 256 + (b2 & 0xff);

            if (j != k || i != k)
                System.out.println(i + " " + b1 + " " + b2 + " " + j + " " + k);
            
        
        }
        */
      
        
        MP3Player player = new MP3Player(null);
        player.start();
        //player.play("C:\\Program Files\\NetBeans3.6\\sine.mp3", 0f, 2f);
        //player.play("C:\\Program Files\\NetBeans3.6\\860AA40A_06.mp3", 0.1f, 5f);
        player.play("C:\\cygwin\\home\\yke\\audiokeys\\music\\MR Demo MP3s\\John Mellencamp\\860AA40A_06_John_Mellencamp_Human_Wheels_Suzanne_And_The_Jewels.wav.mp3", 5f, 6f);
        
    }
    
}
