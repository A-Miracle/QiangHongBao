package com.ctao.qhb.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.WindowManager;
import android.widget.TextView;

import com.ctao.baselib.utils.BarUtils;
import com.ctao.qhb.R;
import com.ctao.qhb.ui.base.MvpActivity;

import java.util.Calendar;

import butterknife.BindView;

/**
 * Created by A Miracle on 2017/8/25.
 */
public class SplashActivity extends MvpActivity {
    @BindView(R.id.tv_copyright) TextView tvCopyright;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onAfterSetContentLayout(Bundle savedInstanceState) {
        setSwipeBackEnable(false);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvCopyright.setText(getString(R.string.copyright, year));

        loadData();
    }

    @Override
    protected boolean onImmersiveStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return true;
    }

    @Override
    protected boolean onImmersiveNavBar() {
        BarUtils.setNavigationBarColor(this, Color.BLACK);
        return true;
    }

    // 加载数据, 此处模拟延时
    private void loadData() {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... strings) {
                SystemClock.sleep(2200);
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                goHome();
            }
        }.execute();
    }

    /**
     * 跳转到主页面
     */
    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() { }
}