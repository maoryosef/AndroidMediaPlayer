package com.myosef.newMediaPlayer.dataProvider;

import android.content.Context;
import com.myosef.newMediaPlayer.models.AlbumModel;
import com.myosef.newMediaPlayer.models.ArtistModel;
import com.myosef.newMediaPlayer.models.SongModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 17/05/13
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
public class MainDataProvider {
    private static MainDataProvider m_Instance;
    private static final Object m_LockContext = new Object();

    private Context m_Context;
    private List<? extends SongModel> m_CurrentPlayList;
    private Integer m_CurrentPlayedSong;

    private MainDataProvider(Context i_Context) {
        m_Context = i_Context;
    }

    public static MainDataProvider getInstance(Context i_Context) {
        if (m_Instance == null) {
            synchronized (m_LockContext) {
                if (m_Instance == null) {
                    m_Instance = new MainDataProvider(i_Context);
                }
            }
        }

        return m_Instance;
    }

    public void setCurrentPlayedSong(int i_currentPlayedSong) {
        m_CurrentPlayedSong = i_currentPlayedSong;
    }

    public Integer getCurrentPlayedSong() {
        return m_CurrentPlayedSong;
    }

    public List<? extends SongModel> getCurrentPlayList() {
        return m_CurrentPlayList;
    }

    public void queueAllSongs() {
        //TODO queue all songs
    }

    public void queueByArtist(String i_ArtistName) {
        //TODO queue by artist
    }

    public void queueByAlbum(int i_AlbumID) {
        //TODO queue by Album
    }

    public List<? extends AlbumModel> getAlbumsList() {
        //TODO get Albums list
    }

    public List<? extends AlbumModel> getAlbumsListByArtist(String i_ArtistName) {
        //TODO get album by artist
    }

    public List<? extends ArtistModel> getArtistList() {
        //TODO get Artist list
    }
}
