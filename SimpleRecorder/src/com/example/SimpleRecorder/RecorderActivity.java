package com.example.SimpleRecorder;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecorderActivity extends Activity implements AudioRecorderListener{
    Button mRecordButton;
    Button mPlayButton;
    TextView mTextView;
    boolean mIsRecording = false;
    boolean mIsPlaying = false;
    AudioRecorder mRecorder;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRecordButton = (Button) findViewById(R.id.recordButton);
        mPlayButton = (Button) findViewById(R.id.playButton);
        mTextView = (TextView) findViewById(R.id.statusText);

        mTextView.setMovementMethod(new ScrollingMovementMethod());

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("RecorderActivity", "mRecordButton Clicked");
                RecorderActivity.this.mRecordButton.setText("...");
                // Perform action on click
                if (mIsRecording) {
                    Log.d("RecorderActivity", "Stopping recorder");
                    mRecorder.stop();
                } else {
                    Log.d("RecorderActivity", "Starting recorder");
                    if (mRecorder == null) {
                        mRecorder = new AudioRecorderAltImpl(RecorderActivity.this);
                    }

                    mRecorder.record();
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("RecorderActivity", "mPlayButton Clicked");
                RecorderActivity.this.mPlayButton.setText("...");
                if (mIsPlaying) {
                    Log.d("RecorderActivity", "Stopping playback");
                    mRecorder.stop();
                } else {
                    Log.d("RecorderActivity", "Starting playback");
                    mRecorder.play();
                }
            }
        });
    }

    @Override
    public void onStartingRecord() {
        mRecordButton.setText("Stop");
        appendText(mTextView, "Starting to record");
        mIsRecording = true;
    }

    private void appendText(TextView iTextView, String iText) {
        if(iTextView != null){
            iTextView.append("\n" + iText);
            final Layout layout = iTextView.getLayout();
            if(layout != null){
                int scrollDelta = layout.getLineBottom(iTextView.getLineCount() - 1)
                        - iTextView.getScrollY() - iTextView.getHeight();
                if(scrollDelta > 0)
                    iTextView.scrollBy(0, scrollDelta);
            }
        }
    }

    @Override
    public void onFinishedRecord() {
        appendText(mTextView, "Done recording...");
        appendText(mTextView, mRecorder.getFileName());
        mPlayButton.setEnabled(true);
        mRecordButton.setText(R.string.buttonRecord);
        mIsRecording = false;
    }

    @Override
    public void onStartingPlayback() {
        appendText(mTextView, "Starting playback...");
        mPlayButton.setEnabled(true);
        mRecordButton.setEnabled(false);
        mPlayButton.setText("Stop");
        mIsPlaying = true;
    }

    @Override
    public void onFinishedPlayback() {
        appendText(mTextView, "Finished playback...");
        mRecordButton.setText(R.string.buttonRecord);
        mPlayButton.setText(R.string.buttonPlay);
        mRecordButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mIsPlaying = false;
    }

    @Override
    public void onError(Exception e) {
        appendText(mTextView, "Error: " + e.getMessage());
        mRecordButton.setText(R.string.buttonRecord);
        mRecordButton.setEnabled(true);
        mIsPlaying = false;
        mIsRecording = false;
    }
}
