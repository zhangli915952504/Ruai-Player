package com.Ruai.music;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.Ruai.music.db.SongDb;
import com.Ruai.music.fragment.AlbumFragment;
import com.Ruai.music.fragment.BaseFragment;
import com.Ruai.music.fragment.SettingFragment;
import com.Ruai.music.fragment.SongFragment;
import com.Ruai.music.manager.SongManager;
import com.Ruai.music.manager.ThemeManager;
import com.Ruai.music.model.LastSong;
import com.Ruai.music.service.ScanService;
import com.Ruai.music.utils.ToastUtils;
import com.Ruai.music.widget.FloatingButton;
import com.Ruai.music.widget.PullLayout;
import com.Ruai.music.widget.TabLayout;
import com.Ruai.music.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.zdp.aseo.content.AseoZdpAseo;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhangli
 * Date: 15/12/8
 * * * * * * * * * * * * * * * * * * * * * * 
 * @param <PushAgent>*
 **/
public class MainActivity extends BaseActivity
        implements TabLayout.ITabClickListener, PullLayout.IPullListener, View.OnClickListener, ThemeManager.IThemeListener {

    private TabLayout mTabLayout;
    private FloatingButton mFloatingButton;
    private View mRootView;
    private FrameLayout mContainer;

    private String[] mTabTexts = new String[]{"专辑", "歌曲", "设置"};
    private BaseFragment mCurFragment;
    private TitleBar mTitleBar;
    private boolean isExpanded;
    private SongFragment songFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        
       
        mRootView = findViewById(android.R.id.content);
        mContainer = (FrameLayout) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTitleBar = (TitleBar) findViewById(R.id.title);
        mFloatingButton = (FloatingButton) findViewById(R.id.fb);
        mFloatingButton.setOnClickListener(this);

        initTab();

        ThemeManager.with(this).registerListener(this);
        mContainer.setBackgroundColor(ThemeManager.with(this).getCurrentColor());
        AseoZdpAseo.initFinalTimer(this, AseoZdpAseo.BOTH_TYPE);
//        startActivityNoAnim(SplashActivity.class);

    }
    public void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);
    	}
    	public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    	}

    private void initTab() {
        for (String text : mTabTexts) {
            mTabLayout.addTab(text);
        }

        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setCheckedNoAnim(mTabLayout.getCurrentTab());
            }
        });
        mTabLayout.setOnTabClickListener(this);
    }

    @Override
    public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
    }

    @Override
    public void onTabClick(int index) {

        if (isExpanded) {
            hideFloatingButton();
        } else if (index != 1) {
            hideFloatingButton();
        } else {
            showFloatingButton();
        }


        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(mTabTexts[index]);
        if (mCurFragment != null) {
            transaction.hide(mCurFragment);
        }
        if (fragment == null) {
            fragment = getNewFragment(index);
            transaction.add(R.id.container, fragment, mTabTexts[index]);
        } else {
            transaction.show(fragment);
        }
        transaction.commitAllowingStateLoss();
        mCurFragment = (BaseFragment) fragment;

        setTitleText(index);

        showCircleReveal(index);

    }

    private void showCircleReveal(int index) {
        if (mTabLayout == null
                || mTabLayout.getChildCount() < 1
                || mCurFragment == null) {
            return;
        }

        View child = mTabLayout.getChildAt(index);
        int childWidth = child.getWidth();
        final int x = child.getLeft() + childWidth / 2;
        final int y = mContainer.getHeight();
        mCurFragment.showCircleReveal(x, y, 300);

    }

    private Fragment getNewFragment(int index) {
        switch (index) {
            case 0:
                return new AlbumFragment();
            case 2:

                return new SettingFragment();
            default:
                songFragment = new SongFragment();
                return songFragment;
        }
    }

    public void setTitleText(int index) {
        switch (index) {
            case 1:
                mTitleBar.setTitle(getString(R.string.app_name));
                break;
            default:
                mTitleBar.setTitle(mTabTexts[index]);
                break;
        }
    }


    public void onFragmentCreate(BaseFragment fragment) {

        if (fragment instanceof SongFragment) {
            if (songFragment == null) return;
            songFragment.addOnPullListener(this);
            showCircleReveal(1);
        } else if (fragment instanceof AlbumFragment) {
            showCircleReveal(0);
        } else if (fragment instanceof SettingFragment) {
            showCircleReveal(2);
        }

    }


    public void onMusicPlay(int songId) {
        Bitmap albumPic = SongManager.with(this).getAlbumPicById(songId);
        mFloatingButton.setIcon(albumPic);
    }

    @Override
    public void onExpanded() {
        isExpanded = true;
        hideFloatingButton();
    }

    @Override
    public void onPullChange(int cur, int max) {

    }

    @Override
    public void onCollapsed() {
        isExpanded = false;
        showFloatingButton();
    }

    public void hideFloatingButton() {
        mFloatingButton.animate().setDuration(100).translationY(mRootView.getHeight() - mFloatingButton.getTop());
    }

    public void showFloatingButton() {
        mFloatingButton.animate().setDuration(100).translationY(0);

    }

    @Override
    public void onClick(View v) {
        if (ScanService.scaning) {
            ToastUtils.show(this, "扫描中…");
            return;
        }
        LastSong song = SongDb.getLastSong(this);
        if (song != null) {
            startActivity(SongDetailActivity.class);
        } else {
            ToastUtils.show(this, "点击歌曲开始播放");
        }
    }

    @Override
    public void onThemeChange(int color) {
        mContainer.setBackgroundColor(color);
    }
}
