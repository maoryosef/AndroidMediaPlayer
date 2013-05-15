package com.example.AndroidMediaPlayer;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 15/05/13
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public class Utilities {

    public static String getDurationStringFromMilliseconds(Long i_Duration) {
        i_Duration /= 1000;

        return String.format("%02d:%02d", i_Duration / 60, i_Duration % 60);
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
