package com.example.AndroidMediaPlayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 15/05/13
 * Time: 19:44
 * To change this template use File | Settings | File Templates.
 */
public class SongsManager2 {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private static final String LOG_TAG = SongsManager2.class.getName();
    private Activity m_ParentActivity;


    public SongsManager2(Activity i_ParentActivity) {
        m_ParentActivity = i_ParentActivity;
    }

    public void rescanMedia() {
        Toast.makeText(m_ParentActivity, "Scanning media...", Toast.LENGTH_LONG).show();
        m_ParentActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    public ArrayList<HashMap<String, String>> getPlayList() {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor songsCursor = m_ParentActivity.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, null
        );

        while(songsCursor.moveToNext()) {
            int count = songsCursor.getColumnCount();

            HashMap<String, String> song = new HashMap<String, String>();
            for (int i = 0; i < count; i++) {
                if (songsCursor.getColumnName(i).equals(MediaStore.Audio.Media.DURATION)) {
                    Long duration = Long.parseLong(songsCursor.getString(i));
                    String durationString = Utilities.getDurationStringFromMilliseconds(duration);

                    song.put(songsCursor.getColumnName(i), durationString);
                } else {
                    song.put(songsCursor.getColumnName(i), songsCursor.getString(i));
                }
            }

            songsList.add(song);
        }

        return songsList;
    }
}