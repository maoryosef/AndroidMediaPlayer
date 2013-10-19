package com.example.SimpleRecorder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 18/10/13
 * Time: 17:04
 */
public class AudioRecorderImpl implements AudioRecorder, Runnable {
    private Thread mThread;
    private boolean mInAction = false;
    private AudioRecorderListener mListener;
    private MediaRecorder mRecorder;
    private static String mFileName;
    private MediaPlayer mPlayer;

    public AudioRecorderImpl(AudioRecorderListener iListener) {
        mListener = iListener;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    @Override
    public void start() {
        if (!mInAction) {
            Log.d("AudioRecorderImpl", "Creating new thread...");
            mThread = new Thread(this);
            Log.d("AudioRecorderImpl", "Starting Thread");
            mInAction = true;
            doOnStartRecord();
            mThread.start();
        } else {
            Log.d("AudioRecorderImpl", "Already in action, ignoring request");
        }
    }

    private void doOnStartRecord() {
        runMethod(mListener, "onStartingRecord", null);
    }

    private void doOnFinishRecord() {
        runMethod(mListener, "onFinishedRecord", null);
    }

    private void doOnStartPlayback() {
        runMethod(mListener, "onStartingPlayback", null);
    }

    private void doOnFinishPlayback() {
        mInAction = false;
        runMethod(mListener, "onFinishedPlayback", null);
    }

    private void doOnError(Exception e) {
        mInAction = false;
        runMethod(mListener, "onError", new Class<?>[] {Exception.class}, e);
    }

    private void runMethod(final Object iListener, final String iMethodName, final Class<?>[] iParameterType, final Object... iArgs) {
        if(iListener != null) {
            if(iListener instanceof Activity)
            {
                Activity activity = (Activity) iListener;
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        try {
                            iListener.getClass().getMethod(iMethodName, iParameterType).invoke(iListener, iArgs);
                        } catch (Exception e) {
                            Log.e("AudioRecorderImpl", "Error while invoking method", e);
                        }
                    }
                });
            }
            else {
                try {
                    iListener.getClass().getMethod(iMethodName).invoke(iListener, iArgs);
                } catch (Exception e) {
                    Log.e("AudioRecorderImpl", "Error while invoking method", e);
                }
            }
        }
    }


    @Override
    public void stop() {
        mInAction = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        doOnFinishRecord();
    }

    @Override
    public void play() {
        doOnStartPlayback();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    doOnFinishPlayback();
                }
            });

            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("AudioRecorderImpl", "prepare() failed", e);
            doOnError(e);
        }
    }

    @Override
    public String getFileName() {
        return mFileName;
    }

    @Override
    public void run() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e("AudioRecorderImpl", "prepare() failed", e);
            doOnError(e);
            mInAction = false;
        }
    }
}
