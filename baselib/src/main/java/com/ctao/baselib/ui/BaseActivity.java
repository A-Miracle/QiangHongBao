package com.ctao.baselib.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.afollestad.appthemeengine.ATE;
import com.ctao.baselib.R;
import com.ctao.baselib.lib.swipebacklayout.app.SwipeBackActivity;
import com.ctao.baselib.manager.AppManager;
import com.ctao.baselib.utils.BarUtils;
import com.ctao.baselib.utils.LogUtils;

/**
 * Created by A Miracle on 2017/6/24.
 * 模板方法模式
 */
public abstract class BaseActivity extends SwipeBackActivity {
    public static final String TYPE_THEME_COLOR = "TYPE_THEME_COLOR";

    protected int themeColor; // 主题颜色
    protected boolean isNeedTheme = true;

    @Override
    public void finish() {
        AppManager.getInstance().removeActivity(this);
        unregisterEventBus();
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        LogUtils.printOut("leak : " + getClass().getSimpleName() + ".finish() >>> ");
    }

    @Override
    protected void onDestroy() {
        unbindButterKnife();
        super.onDestroy();
        LogUtils.printOut("leak : " + getClass().getSimpleName() + ".onDestroy() >>> ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme(); // 一定要在onCreate之前, 否则无效
        super.onCreate(savedInstanceState);
        onBeforeSetContentLayout();

        // setContentView
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        } else {
            View view = createContentView();
            if (view != null) {
                setContentView(view);
            }
        }
        initThemeComplete(); // 使用了一个开源的动态主题切换, 需要这个步骤
        bindButterKnife(); // ButterKnife
        registerEventBus(); // EventBus
        initData(); // 初始数据
        initOrientation(); // 横竖屏
        initSkin();  // 皮肤

        onAfterSetContentLayout(savedInstanceState);

        initToolbar(); // Toolbar
        immersiveStatuNavBar(); // 沉浸式状态栏
        initOtherOnCreateInLast(); // 其他挂载插件

        AppManager.getInstance().addActivity(this);
        LogUtils.printOut("leak : " + getClass().getSimpleName() + "--- 已开启 >>> ");
    }

    public View getContentView(){
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**注册EventBus*/
    protected void registerEventBus() {}

    /**反注册EventBus*/
    protected void unregisterEventBus() {}

    /**初始化数据, 或获取Intent传递的值*/
    protected void initData() {}

    /**横竖屏控制*/
    protected void initOrientation() {
        // 竖屏: SCREEN_ORIENTATION_PORTRAIT
        // 横屏: SCREEN_ORIENTATION_LANDSCAPE
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**设置皮肤*/
    protected void initSkin() {
        /*//根据SwipeBackActivity的设置, DecorView 不能有背景, WindowBackground也应为透明
        View contentView = getContentView();
        if (this instanceof MainActivity) {
            UIUtils.setupSkin(contentView);
        } else {
            UIUtils.setupSkinBlurry(contentView);
        }*/
    }

    /**setContent之前*/
    protected void onBeforeSetContentLayout() { }

    /**获取 setContent Id*/
    protected abstract int getLayoutId();

    /**直接 setContent View*/
    protected View createContentView() {
        return null;
    }

    /**通过注解绑定控件, 在使用ButterKnife可能的情况下*/
    protected void bindButterKnife() { }

    /**通过注解绑定控件, 在使用ButterKnife可能的情况下*/
    protected void unbindButterKnife() { }

    /**setContent之后*/
    protected void onAfterSetContentLayout(Bundle savedInstanceState) { }

    /**设置 ActionBar*/
    protected void initToolbar(){
        Toolbar toolbar =  getBackToolBar();
        if(toolbar != null){
            // 添加返回
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**获取设置ActionBar*/
    protected Toolbar getBackToolBar(){ return null; }

    /**沉浸式状态栏（4.4以上系统有效） */
    protected void immersiveStatuNavBar(){
        if(!onImmersiveStatusBar()){
            // 状态栏
            BarUtils.setStatusBarColor(this, themeColor);
        }
        if(!onImmersiveNavBar()){
            // 导航栏
            BarUtils.setNavigationBarColor(this, Color.BLACK);
        }
    }

    /**子类自己处理沉浸式状态栏, return true*/
    protected boolean onImmersiveStatusBar(){
        return false;
    }

    /**子类自己处理导航栏, return true*/
    protected boolean onImmersiveNavBar(){
        return false;
    }

    /**挂载其他视图*/
    protected void initOtherOnCreateInLast() {
        /*if(this instanceof MainActivity){
            OtherPendantUtils.addPendant(this, getContentView());
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void finalize() throws Throwable {
        /*
        这里用到的一个知识点就是Java中Object类的finalize方法。
        当GC准备回收一个Java Object（所有Java对象都是Object的子类）的时候，
        GC会调用这个Object的finalize方法。
        这个方法有点类似于C++中析构函数，
        本意是让你用来回收一些已经不需要的资源的（主要是针对Native资源）。
        其实Java日常开发中，并不鼓励依赖于这个方法来实现回收的逻辑，
        因为如果你重度依赖于finalize的话，finalize本身也有可能造成内存泄漏，
        但是我们这里只是用来作为是否已经回收的依据，还是可以的。
         */
        LogUtils.printOut("leak : " + getClass().getSimpleName() + "--- 已回收 >>> ");
        super.finalize();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //----------------------Theme处理-------------------------------------------
    private long updateTime = -1L;

    @Nullable
    public abstract String getATEKey();

    /**设置自定义主题*/
    protected void initTheme() {
        if(isNeedTheme){
            ATE.preApply(this, getATEKey());
        }
    }

    /**设置自定义主题*/
    protected void initThemeComplete() {
        if(isNeedTheme){
            this.updateTime = System.currentTimeMillis();
            ATE.apply(this, getATEKey());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果值是应用/承诺(配置)创建活动以来,现在重新创建它
        //第三个参数是可选配置的关键。
        if(isNeedTheme && ATE.didValuesChange(this, updateTime, getATEKey())) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 着色适用于菜单图标和溢出按钮在必要时(如工具栏背景是浅色)
        if(isNeedTheme && menu.size() > 0) {
            ATE.applyMenu(this, getATEKey(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 着色适用于小部件(如复选框)内溢出菜单弹出
        if(isNeedTheme){
            ATE.applyOverflow(this, getATEKey());
        }
        return super.onPrepareOptionsMenu(menu);
    }
    //----------------------Theme处理-------------------------------------------
}
