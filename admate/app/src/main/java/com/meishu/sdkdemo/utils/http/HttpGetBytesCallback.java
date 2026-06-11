package com.meishu.sdkdemo.utils.http;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface HttpGetBytesCallback {

    void onFailure(@NotNull IOException e);

    void onResponse(HttpResponse<byte[]> httpResponse) throws IOException;
}
