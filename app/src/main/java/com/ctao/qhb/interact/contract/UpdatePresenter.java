package com.ctao.qhb.interact.contract;

import com.ctao.baselib.Global;
import com.ctao.baselib.manager.ThreadManager;
import com.ctao.baselib.utils.FileUtils;
import com.ctao.baselib.utils.IOUtils;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.Config;
import com.ctao.qhb.interact.model.Update;
import com.ctao.qhb.network.retrofit2.RetrofitConfig;
import com.ctao.qhb.network.retrofit2.RetrofitFactory;
import com.ctao.qhb.network.service.UpdateApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by A Miracle on 2017/8/29.
 */
public class UpdatePresenter implements IUpdateContract.Presenter {

    private final IUpdateContract.View mUpdateView;
    private final UpdateApiService mService;

    public UpdatePresenter(IUpdateContract.View updateView) {
        this.mUpdateView = updateView;
        Retrofit retrofit = RetrofitFactory.getInstance().getRetrofit(RetrofitConfig.URL_GIT_HUB);
        mService = retrofit.create(UpdateApiService.class);
    }

    @Override
    public void checkUpdate() {
        mUpdateView.showProgress();
        Call<Update> call = mService.checkUpdate();
        call.enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {
                mUpdateView.hideProgress();
                if(response.isSuccessful()){
                    mUpdateView.checkUpdate(response.body());
                    return;
                }
                mUpdateView.showFailure("请求失败！", "checkUpdate");
            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {
                mUpdateView.hideProgress();
                mUpdateView.showFailure("请求失败！", "checkUpdate");
            }
        });
    }

    @Override
    public void downloadApk(final String fileName) {
        mUpdateView.downloadProgress(0);
        ThreadManager.getDownloadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ResponseBody> call = mService.downloadAPk(fileName);
                    Response<ResponseBody> execute = call.execute();
                    LogUtils.printOut("Download", "server contacted and has file");
                    boolean success = false;
                    if (execute.isSuccessful() && writeResponseBodyToDisk(execute.body(), fileName)) {
                        success = true;
                    }
                    LogUtils.printOut("Download", "file download was a success? " + success);
                    final boolean finalSuccess = success;
                    Global.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalSuccess) {
                                File file = new File(FileUtils.getExternalFilesDir(Config.FILE_APK) + File.separator + fileName);
                                mUpdateView.downloadComplete(file);
                            } else {
                                mUpdateView.showFailure("Apk下载失败！", "downloadApk");
                            }
                        }
                    });
                } catch (IOException e) {
                    LogUtils.printOut("Download", "error :" + e.getMessage());
                    Global.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateView.showFailure("Apk下载失败！", "downloadApk");
                        }
                    });
                }
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        File file = new File(FileUtils.getExternalFilesDir(Config.FILE_APK) + File.separator + fileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(file);

            byte[] fileReader = new byte[1024 * 512]; // 0.5M

            final long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;

                // TODO 这里去更新进度怎么都不行, 通过Handler发送到UI怎么都没动静. 就连Log都是最后一次性打印出来的

                LogUtils.printOut("saveFile", "file download: " + fileSizeDownloaded + " of " + fileSize);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(outputStream);
        }
    }
}
