package com.example.SimpleRecorder;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 18/10/13
 * Time: 17:24
 */
public interface AudioRecorderListener {
    public void onStartingRecord();
    public void onFinishedRecord();
    public void onStartingPlayback();
    public void onFinishedPlayback();
    public void onError(Exception e);
}
