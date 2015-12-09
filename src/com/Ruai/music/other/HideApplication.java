package com.Ruai.music.other;

import android.app.Application;

import com.Ruai.music.db.SongDb;
import com.Ruai.music.manager.SongManager;
import com.Ruai.music.model.LastSong;
import com.Ruai.music.utils.SharedUtils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/10/21
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HideApplication extends Application {

    public static boolean showListAnim;

    @Override
    public void onCreate() {
        super.onCreate();

        showListAnim = SharedUtils.getBoolean(this, Constants.KEY_SHOW_LIST_ANIM, true);

        LastSong lastSong = SongDb.getLastSong(this);
        if (lastSong != null) {
            SongManager.with(this).setCurrentSong(lastSong.getId());
        }
    }
}
