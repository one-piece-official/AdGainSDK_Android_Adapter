package com.meishu.sdkdemo.utils.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.widget.ImageView;

import com.meishu.sdkdemo.utils.SdkHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    private static OkHttpClient client;

    private static void initCliend() {
        if (client != null) {
            return;
        }
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void asyncGetImage(String url, ImageView iv) {
        iv.setTag(url);
        SoftReference<ImageView> softReference = new SoftReference<>(iv);
        HttpUtil.asyncGetFile(url, new HttpGetBytesCallback() {
            @Override
            public void onFailure(@NotNull IOException e) {
            }

            @Override
            public void onResponse(HttpResponse<byte[]> httpResponse) throws IOException {
                try {
                    if (httpResponse.isSuccessful()) {

                        byte[] responseBody = httpResponse.getResponseBody();
                        if (responseBody != null && responseBody.length > 0) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                            if (bitmap != null) {
                                ImageView imageView = softReference.get();
                                if (imageView == null) {
                                    return;
                                }
                                if (imageView.getTag() != null && imageView.getTag().equals(url)) {
                                    imageView.setImageBitmap(bitmap);
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                }
            }
        });
    }

    public static void asyncGetFile(String url, @NotNull final HttpGetBytesCallback callback) {

        try {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            if (client == null) {
                initCliend();
            }
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, final IOException e) {
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        SdkHandler.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    callback.onFailure(e);
                                } catch (Throwable ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } else {
                        try {
                            callback.onFailure(e);
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final HttpResponse<byte[]> httpResponse = new HttpResponse<>();

                    if (response.isSuccessful()) {

                        if (response.body() != null) {
                            byte[] bytes = response.body().bytes();
                            httpResponse.setSuccessful(true);
                            httpResponse.setResponseBody(bytes);

                        } else {
                            httpResponse.setSuccessful(false);
                            httpResponse.setErrorCode(response.code());
                            httpResponse.setErrorDescription("bad file");
                        }

                    } else {
                        httpResponse.setSuccessful(false);
                        httpResponse.setErrorCode(response.code());
                        httpResponse.setErrorDescription(response.message());
                    }
                    response.close();
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        SdkHandler.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    callback.onResponse(httpResponse);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        try {
                            callback.onResponse(httpResponse);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (final Exception e) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                SdkHandler.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(new IOException("get file error"));
                    }
                });
            } else {
                try {
                    callback.onFailure(new IOException("get file error"));
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
            e.printStackTrace();
        }

    }
}
