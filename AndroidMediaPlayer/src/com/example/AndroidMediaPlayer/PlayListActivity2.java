package com.example.AndroidMediaPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 15/05/13
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class PlayListActivity2 extends ListActivity {
    private static final String LOG_TAG = PlayListActivity2.class.getName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        Log.d(LOG_TAG, "OnCreate");

        /*Toast.makeText(this, "Re - Scanning media...", Toast.LENGTH_LONG).show();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));*/

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor songsCursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                          projection,
                                          null, null,null
                                          );

        while(songsCursor.moveToNext()) {
            int count = songsCursor.getColumnCount();
            String message = "";

            for (int i = 0; i < count; i++) {
                message += songsCursor.getColumnName(i) + "=" + songsCursor.getString(i) + " || ";
            }


            Log.d(LOG_TAG, message);
        }

        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.playlist_item2, songsCursor, new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST}, new int[] { R.id.songTitle, R.id.artistName });

                /*(this, songsCursor, R.layout.playlist_item,
                new String[] {"songTitle"}, new int[] { R.id.songTitle });*/

        setListAdapter(adapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PlayListActivity2.this);

                dlgAlert.setTitle("click test");
                dlgAlert.setMessage("Song index : " + songIndex);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        });

    }
}