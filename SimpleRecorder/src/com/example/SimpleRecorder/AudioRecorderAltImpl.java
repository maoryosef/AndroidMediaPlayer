package com.example.SimpleRecorder;

import android.app.Activity;
import android.media.*;
import android.os.Environment;
import android.util.Log;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 18/10/13
 * Time: 17:04
 */
public class AudioRecorderAltImpl implements AudioRecorder{
    private Thread mRecordingThread;
    private Thread mPlayerThread;
    private boolean mInAction = false;
    private AudioRecorderListener mListener;
    private AudioRecord mRecorder;
    private String mFileName;
    private AudioTrack mAudioTrackPlayer;

    private static final int RECORDER_SAMPLERATE = 11025;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int PLAYER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private int mBufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int mBytesPerElement = 2; // 2 bytes in 16bit format

    public String getFileName() {
        return mFileName;
    }

    public AudioRecorderAltImpl(AudioRecorderListener iListener) {
        mListener = iListener;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.pcm";
    }

    public void startRecording() {
        Log.d("AudioRecorderAltImpl", "Starting to record");
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, mBufferElements2Rec * mBytesPerElement);

        mRecorder.startRecording();
        mInAction = true;
        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        mRecordingThread.start();
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        short sData[] = new short[mBufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(mFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (mInAction) {
            mRecorder.read(sData, 0, mBufferElements2Rec);
            Log.d("AudioRecorderAltImpl", "Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, mBufferElements2Rec * mBytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (!mInAction) {
            startRecording();
            doOnStartRecord();
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
        if (null != mRecorder) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordingThread = null;
            doOnFinishRecord();
        }

        if (mAudioTrackPlayer != null) {
            mAudioTrackPlayer.stop();
        }
    }

    @Override
    public void play() {
        doOnStartPlayback();
        mInAction = true;
        mPlayerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    playAudioFileViaAudioTrack(AudioRecorderAltImpl.this.getFileName());
                } catch (IOException e) {
                    Log.e("AudioRecorderImpl", "Player failed", e);
                    doOnError(e);
                }
            }
        }, "AudioPlayer Thread");

        mPlayerThread.start();
    }

    private void playAudioFileViaAudioTrack(String filePath) throws IOException
    {
        // We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath==null) {
            return;
        }

        int intSize = android.media.AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, PLAYER_CHANNELS,
                RECORDER_AUDIO_ENCODING);

        mAudioTrackPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, PLAYER_CHANNELS,
                RECORDER_AUDIO_ENCODING, intSize, AudioTrack.MODE_STREAM);


        if (mAudioTrackPlayer == null){
            Log.d("AudioRecorderImpl", "audio track is not initialised ");
            return;
        }

        int count = 512 * 1024; // 512 kb
        //Reading the file..
        byte[] byteData = null;
        File file = null;
        file = new File(filePath);

        byteData = new byte[(int)count];
        FileInputStream in = null;

        in = new FileInputStream( file );

        int bytesread = 0, ret = 0;
        int size = (int) file.length();
        mAudioTrackPlayer.play();
        while (bytesread < size && mInAction) {
            ret = in.read( byteData,0, count);
            if (ret != -1) { // Write the byte array to the track
                mAudioTrackPlayer.write(byteData,0, ret);
                bytesread += ret;
            } else {
                break;
            }
        }

        in.close();
        mAudioTrackPlayer.stop();
        mAudioTrackPlayer.release();
        mAudioTrackPlayer = null;
        doOnFinishPlayback();
    }
}
