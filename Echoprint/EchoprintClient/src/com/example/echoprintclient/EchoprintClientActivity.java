package com.example.echoprintclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.echoprint.AudioFingerprinter;

import java.util.Hashtable;

public class EchoprintClientActivity extends Activity implements AudioFingerprinter.AudioFingerprinterListener{
    boolean recording, resolved;
    AudioFingerprinter fingerprinter;
    TextView status;
    Button btn;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = (Button) findViewById(R.id.recordButton);
        status = (TextView) findViewById(R.id.status);

        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                EchoprintClientActivity.this.btn.setText("...");
                // Perform action on click
                if(recording)
                {
                    fingerprinter.stop();
                }
                else
                {
                    if(fingerprinter == null)
                        fingerprinter = new AudioFingerprinter(EchoprintClientActivity.this);

                    fingerprinter.fingerprint(20);
                }
            }
        });
    }

    public void didFinishListening()
    {
        btn.setText("Start");

        if(!resolved)
            status.setText("Idle...");

        recording = false;
    }

    public void didFinishListeningPass()
    {}

    public void willStartListening()
    {
        status.setText("Listening...");
        btn.setText("Stop");
        recording = true;
        resolved = false;
    }

    public void willStartListeningPass()
    {}

    public void didGenerateFingerprintCode(String code)
    {
        status.setText("Will fetch info for code starting:\n" + code.substring(0, Math.min(50, code.length())));
    }

    public void didFindMatchForCode(final Hashtable<String, String> table,
                                    String code)
    {
        resolved = true;
        status.setText("Match: \n" + table);
    }

    public void didNotFindMatchForCode(String code)
    {
        resolved = true;
        status.setText("No match for code starting with: \n" + code.substring(0, Math.min(50, code.length())));
    }

    public void didFailWithException(Exception e)
    {
        resolved = true;
        status.setText("Error: " + e);
    }
}
