package com.code.library.http.builder;

import com.code.library.http.OkHttpUtils;
import com.code.library.http.request.OtherRequest;
import com.code.library.http.request.RequestCall;

public class HeadBuilder extends GetBuilder {
    @Override
    public RequestCall build() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers).build();
    }
}
