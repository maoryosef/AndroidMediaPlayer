package com.example.AndroidMediaPlayer;

import android.app.AlertDialog;
import android.app.ListActivity;
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
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class PlayListActivity2 extends ListActivity {
    private static final String LOG_TAG = PlayListActivity2.class.getName();
    private ArrayList<HashMap<String, String>> m_SongsListData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist2);

        Log.d(LOG_TAG, "OnCreate");

        SongsManager2 songsManager = new SongsManager2(this);

        m_SongsListData = songsManager.getPlayList();

        //TODO create song item class
        //TODO create song item adapter
        ListAdapter adapter = new SimpleAdapter(this, m_SongsListData, R.layout.playlist_item2,
                new String[] {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION}, new int[] { R.id.songTitle, R.id.artistName, R.id.duration });


        setListAdapter(adapter);

        ListView lv = getListView();

        int currentSongIndex = getIntent().getExtras().getInt("songIndex");

        /*m_SongsListData.get(currentSongIndex).put(MediaStore.Audio.Media.TITLE, "YES");

        ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();*/

/*        View view = adapter.getView(currentSongIndex, null, lv);

        if (view == null) {
            Log.d(LOG_TAG, "FAIL!!");
        } else {
            Log.d(LOG_TAG, "Success!!");
            TextView tv = (TextView)view.findViewById(R.id.songTitle);
            Log.d(LOG_TAG, tv.getText().toString());
            tv.setText("bla bla");

            tv = (TextView)view.findViewById(R.id.songTitle);
            Log.d(LOG_TAG, tv.getText().toString());

            view = adapter.getView(currentSongIndex, view, lv);

            tv = (TextView)view.findViewById(R.id.songTitle);
            Log.d(LOG_TAG, tv.getText().toString());
        }*/

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        AndroidBuildingMusicPlayerActivity.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", songIndex);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        /*int currentSongIndex = getIntent().getExtras().getInt("songIndex");
        ListView lv = getListView();
        View view = lv.getChildAt(currentSongIndex);
        TextView tv = (TextView)view.findViewById(R.id.songTitle);
        tv.setTextColor(4);*/
    }


}