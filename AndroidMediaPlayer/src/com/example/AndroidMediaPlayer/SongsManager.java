package com.example.AndroidMediaPlayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 15/05/13
 * Time: 02:17
 * To change this template use File | Settings | File Templates.
 */
public class SongsManager {
    final String MEDIA_PATH = new String ("/sdcard/");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private static final String SM_TAG = "SONGS_MANAGER";
    private Context m_Context;

    public SongsManager(Context i_Context) {
        m_Context = i_Context;
    }

    public ArrayList<HashMap<String, String>> getPlayList() {
        Log.d(SM_TAG, "Loading " + MEDIA_PATH);

        File home = new File(MEDIA_PATH);

        Toast.makeText(m_Context, "Scanning media...", Toast.LENGTH_LONG).show();
        m_Context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

        //scanDirectory(home);

        return songsList;
    }

    public void scanDirectory(File i_Directory) {
        Log.d(SM_TAG, "Entering " + i_Directory.getPath());
 //       MediaScannerConnection.scanFile(m_Context, new String[]{i_Directory.getPath()},
//                new String[]{ "audio/mp3", "*/*" },
   /*             new MediaScannerConnection.MediaScannerConnectionClient() {
    //                public void onMediaScannerConnected() {
     //                   Log.d(SM_TAG, "Connected");
      //              }

                    public void onScanCompleted(String path, Uri uri) {
                        Log.d(SM_TAG, path);
                    }
                });*/

        addSongFiles(i_Directory);

        File[] subDirs = i_Directory.listFiles(new DirectoryFilter());

        if (subDirs != null) {
            for (File dir : subDirs) {
                scanDirectory(dir);
            }
        }
    }

    private void addSongFiles(File i_Directory) {
        Log.d(SM_TAG, "Searching for song files");

        File[] songFiles = i_Directory.listFiles(new MP3FilenameFilter());

        if (songFiles != null && songFiles.length > 0) {
            Log.d(SM_TAG, "Found " + songFiles.length + " files");
            for (File file : songFiles) {
                Log.d(SM_TAG, "Adding " + file.getName());
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName());
                song.put("songPath", file.getPath());

                songsList.add(song);
            }
        }
    }

    private class MP3FilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String filename) {
            return (filename.endsWith(".mp3")) || filename.endsWith(".MP3");
        }
    }

    private class DirectoryFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
}
