package com.ctao.qhb.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ctao.baselib.utils.FileUtils;
import com.ctao.baselib.utils.SPUtils;
import com.ctao.baselib.utils.ToastUtils;
import com.ctao.baselib.utils.ViewUtils;
import com.ctao.qhb.BuildConfig;
import com.ctao.qhb.Config;
import com.ctao.qhb.R;
import com.ctao.qhb.interact.contract.IUpdateContract;
import com.ctao.qhb.interact.contract.UpdatePresenter;
import com.ctao.qhb.interact.model.Update;
import com.ctao.qhb.ui.base.MvpActivity;
import com.ctao.qhb.utils.UriUtils;

import java.io.File;

import butterknife.BindView;

/**
 * Created by A Miracle on 2017/8/25.
 */
public class AboutActivity extends MvpActivity implements IUpdateContract.View{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private IUpdateContract.Presenter mPresenter;
    private MaterialDialog mProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected Toolbar getBackToolBar() {
        return toolbar;
    }

    @Override
    protected boolean onImmersiveStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 第一步, 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // toolbar 加 高度 加 Margin
            int statusBarHeight = ViewUtils.getStatusBar(this);
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            params.height += statusBarHeight;

            toolbar.setPadding(0, statusBarHeight, 0, 0);
        }
        return true;
    }

    @Override
    protected void onAfterSetContentLayout(Bundle savedInstanceState) {
        mPresenter = new UpdatePresenter(this);
        getFragmentManager().beginTransaction().replace(R.id.fl_container, new AboutFragment()).commit();
    }

    @Override
    public void checkUpdate(final Update update) {
        if(update == null){
            return;
        }

        if(BuildConfig.VERSION_CODE >= update.versionCode){
            ToastUtils.show("当前已是最新版本");
            return;
        }

        SPUtils.putObject(Config.SP_LATEST_CODE, update.versionCode); // 存储最新包Code, 用于安装后删除

        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title("发现新版本：" + update.versionName)
                .content(update.detail)
                .negativeText("取消")
                .positiveText("下载Apk")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which) {
                            case POSITIVE: // 下载Apk
                                File file = new File(FileUtils.getExternalFilesDir(Config.FILE_APK) + File.separator + update.fileName);
                                if (file != null && file.exists()) {
                                    installApk(file); // 下载过了, 直接安装
                                }else {
                                    mPresenter.downloadApk(update.fileName);
                                }
                                break;
                        }

                    }
                })
                .show();
    }

    @Override
    public void downloadProgress(int progress) {
        if (mProgress == null) {
            mProgress = new MaterialDialog.Builder(this)
                    .content("下载中...")
                    .progress(true, 0)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .progressIndeterminateStyle(true)
                    .build();
            mProgress.show();
        }
        mProgress.setProgress(progress);
    }

    @Override
    public void downloadComplete(File file) {
        if(mProgress != null){
            mProgress.dismiss();
            mProgress = null;
        }
        if(file == null){
            return;
        }
        ToastUtils.show("下载完成");
        installApk(file);
    }

    private void installApk(File file) {
        if(file != null){ // 启用安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(Build.VERSION.SDK_INT >= 24){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(UriUtils.fromFile(file), "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }

    @Override
    public void showFailure(String msg, String... tag) {
        super.showFailure(msg, tag);
        if(tag.length > 0 && "downloadApk".equals(tag[0])){
            if(mProgress != null){
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    public static class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_about);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            invalidateSettings();
        }

        private void invalidateSettings() {
            // 当前版本
            findPreference("version").setSummary("v " + BuildConfig.VERSION_NAME);

            // 检查更新
            findPreference("update").setOnPreferenceClickListener(this);

            // 推荐给朋友
            findPreference("share").setOnPreferenceClickListener(this);

            // 打赏作者
            findPreference("reward").setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "update":
                    checkUpdate();
                    break;
                case "share":
                    share();
                    break;
                case "reward":
                    reward();
                    break;
            }
            return true;
        }

        private void checkUpdate() {
            Activity activity = getActivity();
            if(activity instanceof AboutActivity){
                ((AboutActivity)activity).mPresenter.checkUpdate();
            }
        }

        private void share() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "https://github.com/A-Miracle/QiangHongBao/blob/master/share.md");
            startActivity(Intent.createChooser(intent, "分享"));
        }

        private void reward() {
            new MaterialDialog.Builder(getActivity())
                    .theme(Theme.LIGHT)
                    .title("打赏作者")
                    .content("点击打赏按钮，作者支付宝账号便会自动复制到剪贴板上，此时您可以通过手机支付宝打赏作者。作者跪谢～")
                    .positiveText("打赏")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                            // 将文本复制到剪贴板
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("data", "1243679197@qq.com"));
                            ToastUtils.show("已复制到剪贴板");

                            startAliPay();
                        }

                        private void startAliPay() {
                            Intent intent = getActivity().getPackageManager().
                                    getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                            if (intent != null) {
                                getActivity().startActivity(intent);
                            }
                        }
                    })
                    .show();
        }
    }
}

