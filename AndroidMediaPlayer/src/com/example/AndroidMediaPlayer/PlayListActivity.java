package com.example.AndroidMediaPlayer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 15/05/13
 * Time: 02:37
 * To change this template use File | Settings | File Templates.
 */
public class PlayListActivity extends ListActivity {

    public ArrayList<HashMap<String,String>> songsList = new ArrayList<HashMap<String, String>>();
    private static final String PL_TAG = PlayListActivity.class.getName();

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        Log.d(PL_TAG, "OnCreate");

        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

        SongsManager plm = new SongsManager(this);

        this.songsList = plm.getPlayList();

        for (int i = 0; i < songsList.size(); i++) {
            HashMap<String, String> song = songsList.get(i);

            songsListData.add(song);
        }

        ListAdapter adapter = new SimpleAdapter(this, songsListData, R.layout.playlist_item2,
                                                new String[] {"songTitle"}, new int[] { R.id.songTitle });

        setListAdapter(adapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PlayListActivity.this);

                dlgAlert.setTitle(PlayListActivity.this.songsList.get(songIndex).get("songTitle"));
                dlgAlert.setMessage(PlayListActivity.this.songsList.get(songIndex).get("songPath"));
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        });
    }
}