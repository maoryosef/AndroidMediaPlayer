package com.example.SimpleRecorder;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 18/10/13
 * Time: 17:03
 */
public interface AudioRecorder {
    public void record();
    public void stop();
    public void play();
    public String getFileName();
}
