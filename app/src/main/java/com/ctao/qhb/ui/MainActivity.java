package com.ctao.qhb.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ctao.baselib.manager.AppManager;
import com.ctao.baselib.utils.DateUtils;
import com.ctao.baselib.utils.FileUtils;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.baselib.utils.SPUtils;
import com.ctao.baselib.utils.ToastUtils;
import com.ctao.qhb.BuildConfig;
import com.ctao.qhb.Config;
import com.ctao.qhb.R;
import com.ctao.qhb.event.MessageEvent;
import com.ctao.qhb.interact.contract.IUpdateContract;
import com.ctao.qhb.interact.contract.UpdatePresenter;
import com.ctao.qhb.interact.model.Update;
import com.ctao.qhb.job.config.QQConfig;
import com.ctao.qhb.job.config.TIMConfig;
import com.ctao.qhb.job.config.WeChatConfig;
import com.ctao.qhb.service.QHBService;
import com.ctao.qhb.ui.base.MvpActivity;
import com.ctao.qhb.ui.common.PreferenceActivity;
import com.ctao.qhb.ui.fragment.OtherSettingFragment;
import com.ctao.qhb.ui.fragment.QQSettingFragment;
import com.ctao.qhb.ui.fragment.WeChatSettingFragment;
import com.ctao.qhb.utils.PackageUtils;
import com.ctao.qhb.utils.UriUtils;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by A Miracle on 2017/8/15.
 */
public class MainActivity extends MvpActivity implements IUpdateContract.View{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bt_state) Button bt_state;
    private IUpdateContract.Presenter mPresenter;
    private MaterialDialog mProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onAfterSetContentLayout(Bundle savedInstanceState) {
        mPresenter = new UpdatePresenter(this);
        setSupportActionBar(toolbar);
        changeState();
        getFragmentManager().beginTransaction().replace(R.id.fl_container, new MainFragment()).commit();
        showAgreementDialog();
        deleteHasInstalledPackage();

        String nowDate = DateUtils.formatTime(new Date().getTime(), "yyyyMMdd");
        String oldDate = SPUtils.getString(Config.SP_LATEST_DATE, "");
        if(!nowDate.equals(oldDate)){ // 每天一次检查更新
            SPUtils.putObject(Config.SP_LATEST_DATE, nowDate);
            mPresenter.checkUpdate();
        }
    }

    private void deleteHasInstalledPackage() {
        int code = SPUtils.getInt(Config.SP_LATEST_CODE, -1);
        if(code == -1){
            return;
        }
        if(BuildConfig.VERSION_CODE >= code){ // 已安装最新, 清空apk
            File dir = FileUtils.getExternalFilesDir(Config.FILE_APK);
            if(dir != null && dir.exists() && dir.isDirectory()){
                for (File file : dir.listFiles()) {
                    if(file != null && file.exists() && file.isFile()){
                        file.delete();
                    }
                }
            }
            SPUtils.putObject(Config.SP_LATEST_CODE, -1);
        }
    }

    @OnClick(R.id.bt_state)
    public void onClick(View view){
        if(!QHBService.isRun()){
            openAccessibilityServiceSettings();
        }
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        switch (event.getType()){
            case MessageEvent.QHB_SERVICE_STATE:
                changeState();
                break;
            case MessageEvent.QHB_PACKAGE_INFO_UPDATE:
                showHintDialog();
                break;
        }
    }

    private void showHintDialog() {
        PackageInfo weChat = PackageUtils.getPackageInfo(Config.PACKAGE_NAME_WX);
        PackageInfo qq = PackageUtils.getPackageInfo(Config.PACKAGE_NAME_QQ);
        PackageInfo tim = PackageUtils.getPackageInfo(Config.PACKAGE_NAME_TIM);
        String msg = "";
        if(weChat != null && weChat.versionCode < WeChatConfig.V_1080){
            msg += "微信、";
        }

        if(qq != null && qq.versionCode < QQConfig.V_718){
            msg += "QQ、";
        }

        if(tim != null && tim.versionCode < TIMConfig.V_938){
            msg += "TIM、";
        }

        if (!TextUtils.isEmpty(msg)) {
            msg = msg.substring(0, msg.length() - 1);
            new MaterialDialog.Builder(this)
                    .title("提示")
                    .content("当前" + msg + "版本过低，可能导致抢红包失败！请及时更新到" + msg + "最新版")
                    .positiveText("我知道了")
                    .show();
        }
    }

    /** 打开辅助服务的设置 */
    private void openAccessibilityServiceSettings() {
        new MaterialDialog.Builder(this)
                .title(R.string.open_service_title)
                .customView(R.layout.view_open_service_hint, false)
                .positiveText(R.string.open_service_button)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which) {
                            case POSITIVE:
                                try {
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    startActivity(intent);
                                    ToastUtils.show("找到[快手抢红包]，然后开启服务即可");
                                } catch (Exception e) {
                                    LogUtils.e(e);
                                }
                                break;
                        }

                    }
                })
                .show();
    }

    private void changeState() {
        if(QHBService.isRun()){
            bt_state.setTextColor(Color.parseColor("#666666"));
            bt_state.setText("已连接抢红包服务");
        }else{
            bt_state.setTextColor(Color.parseColor("#F55252"));
            bt_state.setText("抢红包服务中断, 点击开启");
        }
    }

    /** 显示免责声明的对话框 */
    private void showAgreementDialog() {
        boolean isAgreement = SPUtils.getBoolean(Config.SP_AGREEMENT, false);
        if(isAgreement){
            return;
        }
        new MaterialDialog.Builder(this)
                .title(R.string.agreement_title)
                .content(R.string.agreement_message)
                .negativeText("不同意")
                .positiveText("同意")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which) {
                            case NEGATIVE: // 不同意
                                SPUtils.putObject(Config.SP_AGREEMENT, false);
                                finish();
                                break;
                            case POSITIVE: // 同意
                                SPUtils.putObject(Config.SP_AGREEMENT, true);
                                openAccessibilityServiceSettings();
                                break;
                        }

                    }
                })
                .show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    public void checkUpdate(final Update update) {
        if(update == null){
            return;
        }

        if(BuildConfig.VERSION_CODE >= update.versionCode){
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

    @Override
    public void showFailure(String msg, String... tag) {
        if(tag.length > 0 && "checkUpdate".equals(tag[0])){
            return;
        }
        super.showFailure(msg, tag);
        if(tag.length > 0 && "downloadApk".equals(tag[0])){
            if(mProgress != null){
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    @Override
    public void showProgress() {}

    @Override
    public void hideProgress() {}

    public static class MainFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_main);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            invalidateSettings();
        }

        private void invalidateSettings() {
            PackageInfo weChat = PackageUtils.getPackageInfo(Config.PACKAGE_NAME_WX);
            PackageInfo qq = PackageUtils.getPackageInfo(Config.PACKAGE_NAME_QQ);
            PackageInfo tim = PackageUtils.getPackageInfo(Config.PACKAGE_NAME_TIM);

            Preference platform_weChat = findPreference("platform_WeChat"); // 微信开关
            Preference platform_WeChat_setting = findPreference("platform_WeChat_setting"); // 微信设置
            if(weChat == null){
                platform_weChat.setEnabled(false);
                platform_weChat.setSummary("当前手机未安装微信");
                platform_WeChat_setting.setEnabled(false);
                platform_WeChat_setting.setSummary("当前手机未安装微信");
            }else{
                platform_weChat.setOnPreferenceChangeListener(this);
                platform_WeChat_setting.setOnPreferenceClickListener(this);
            }

            Preference platform_QQ = findPreference("platform_QQ"); // QQ开关
            if(qq == null){
                platform_QQ.setEnabled(false);
                platform_QQ.setSummary("当前手机未安装QQ");
            }else{
                platform_QQ.setOnPreferenceChangeListener(this);
            }

            Preference platform_TIM = findPreference("platform_TIM"); // TIM开关
            if(tim == null){
                platform_TIM.setEnabled(false);
                platform_TIM.setSummary("当前手机未安装TIM");
            }else{
                platform_TIM.setOnPreferenceChangeListener(this);
            }

            Preference platform_QQ_setting = findPreference("platform_QQ_setting");// QQ、TIM设置
            if(qq == null && tim == null){
                platform_QQ_setting.setEnabled(false);
                platform_QQ_setting.setSummary("当前手机未安装QQ、TIM");
            }else{
                platform_QQ_setting.setOnPreferenceClickListener(this);
            }

            // 其他设置
            findPreference("setting").setOnPreferenceClickListener(this);

            // 退出服务
            findPreference("exit").setOnPreferenceClickListener(this);

            // 关于
            findPreference("about").setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()){
                case "platform_WeChat":
                    Config.getInstance().changeEnable(Config.SP_ENABLE_WE_CHAT);
                    break;
                case "platform_QQ":
                    Config.getInstance().changeEnable(Config.SP_ENABLE_QQ);
                    break;
                case "platform_TIM":
                    Config.getInstance().changeEnable(Config.SP_ENABLE_TIM);
                    break;
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()){
                case "platform_WeChat_setting":
                    getActivity().startActivity(new Intent(getActivity(), PreferenceActivity.class)
                            .putExtra(PreferenceActivity.TYPE_FRAGMENT, WeChatSettingFragment.class));
                    break;
                case "platform_QQ_setting":
                    getActivity().startActivity(new Intent(getActivity(), PreferenceActivity.class)
                            .putExtra(PreferenceActivity.TYPE_FRAGMENT, QQSettingFragment.class));
                    break;
                case "setting":
                    getActivity().startActivity(new Intent(getActivity(), PreferenceActivity.class)
                            .putExtra(PreferenceActivity.TYPE_FRAGMENT, OtherSettingFragment.class));
                    break;
                case "exit":
                    exit();
                    break;
                case "about":
                    getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
                    break;
            }
            return true;
        }

        private void exit() {
            if(!QHBService.isRun()){
                AppManager.getInstance().exitApp(true);
                return;
            }
            new MaterialDialog.Builder(getActivity())
                    .title("退出服务")
                    .content("找到[快手抢红包]，然后关闭服务再退出即可")
                    .negativeText("取消")
                    .positiveText("确认")
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            switch (which) {
                                case POSITIVE:
                                    getActivity().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                                    break;
                            }

                        }
                    })
                    .show();
        }
    }
}
